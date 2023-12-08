package ru.naumen.personalfinancebot.handler.command;

import ru.naumen.personalfinancebot.handler.event.HandleCommandEvent;
import ru.naumen.personalfinancebot.message.Message;
import ru.naumen.personalfinancebot.model.Category;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.Operation;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Обработчик команды, которая добавляет операцию и отправляет пользователю сообщение
 *
 * @author Aleksandr Kornilov
 */
public class AddOperationHandler implements CommandHandler {
    /**
     * Тип категории, с которым будет работать обработчик
     */
    private final CategoryType categoryType;

    /**
     * Хранилище пользователей
     */
    private final UserRepository userRepository;

    /**
     * Хранилище категорий
     */
    private final CategoryRepository categoryRepository;

    /**
     * Хранилище операций
     */
    private final OperationRepository operationRepository;

    public AddOperationHandler(CategoryType categoryType, UserRepository userRepository,
                               CategoryRepository categoryRepository, OperationRepository operationRepository) {
        this.categoryType = categoryType;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    /**
     * Метод, вызываемый при получении команды
     */
    @Override
    public void handleCommand(HandleCommandEvent event) {
        if (event.getArgs().size() != 2) {
            event.getBot().sendMessage(event.getUser(), Message.INCORRECT_OPERATION_ARGS_AMOUNT);
            return;
        }
        Operation operation;
        try {
            operation = createOperationRecord(event.getUser(), event.getArgs(), categoryType);
        } catch (CategoryRepository.CategoryDoesNotExist e) {
            event.getBot().sendMessage(event.getUser(), Message.CATEGORY_DOES_NOT_EXISTS);
            return;
        } catch (NumberFormatException e) {
            event.getBot().sendMessage(event.getUser(), Message.INCORRECT_PAYMENT_ARG);
            return;
        } catch (IllegalArgumentException e) {
            event.getBot().sendMessage(event.getUser(), Message.ILLEGAL_PAYMENT_ARGUMENT);
            return;
        }
        double currentBalance = event.getUser().getBalance() + operation.getPayment();
        User user = event.getUser();
        user.setBalance(currentBalance);
        userRepository.saveUser(user);
        String message = categoryType == CategoryType.INCOME
                ? Message.ADD_INCOME_MESSAGE
                : Message.ADD_EXPENSE_MESSAGE;
        event.getBot().sendMessage(user,
                message + operation.getCategory().getCategoryName());

    }

    /**
     * Метод для записи в базу операции;
     *
     * @param user Пользователь
     * @param args Аргументы, переданные с командой
     * @param type Расход/Бюджет.
     * @return Совершенная операция
     */
    private Operation createOperationRecord(User user, List<String> args, CategoryType type)
            throws CategoryRepository.CategoryDoesNotExist {
        double payment = Double.parseDouble(args.get(0));
        if (payment <= 0) {
            throw new IllegalArgumentException();
        }
        String categoryName = args.get(1);
        if (type == CategoryType.EXPENSE) {
            payment = -Math.abs(payment);
        } else if (type == CategoryType.INCOME) {
            payment = Math.abs(payment);
        }
        Optional<Category> category = this.categoryRepository.getCategoryByName(user, type, categoryName);
        if (category.isEmpty()) {
            throw new CategoryRepository.CategoryDoesNotExist();
        }
        return this.operationRepository.addOperation(user, category.get(), payment);
    }
}