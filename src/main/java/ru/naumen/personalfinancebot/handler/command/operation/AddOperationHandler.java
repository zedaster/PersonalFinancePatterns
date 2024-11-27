package ru.naumen.personalfinancebot.handler.command.operation;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.command.ArgumentSplitter;
import ru.naumen.personalfinancebot.handler.command.HandleCommandException;
import ru.naumen.personalfinancebot.handler.command.MultiCommandHandler;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.message.Message;
import ru.naumen.personalfinancebot.model.Category;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.Operation;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.category.exception.NotExistingCategoryException;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;
import ru.naumen.personalfinancebot.service.CategoryParseService;

import java.util.List;
import java.util.Optional;

/**
 * Обработчик команды, которая добавляет операцию и отправляет пользователю сообщение
 * Пример:
 *
 * @author Aleksandr Kornilov
 */
public class AddOperationHandler extends MultiCommandHandler {
    /**
     * Сообщение об успешном добавлении дохода для пользователя
     */
    private static final String ADD_INCOME_MESSAGE = "Вы успешно добавили доход по источнику: ";

    /**
     * Сообщение об успешном добавлении расхода для пользователя
     */
    private static final String ADD_EXPENSE_MESSAGE = "Добавлен расход по категории: ";

    /**
     * Сообщение об отсутствии категории
     */
    private static final String CATEGORY_DOES_NOT_EXISTS =
            "Указанная категория не числится. Используйте команду /add_[income/expense]_category чтобы добавить её";

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

    /***/
    private final CategoryParseService categoryParseService = CategoryParseService.getInstance();


    public AddOperationHandler(CategoryType categoryType, UserRepository userRepository,
                               CategoryRepository categoryRepository, OperationRepository operationRepository) {
        this.categoryType = categoryType;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    protected ArgumentSplitter splitArguments(List<String> arguments) {
        return new OperationSplitter(arguments);
    }

    @Override
    public void handleSingleCommand(CommandData commandData, Session session) throws HandleCommandException {

        Operation operation;
        try {
            operation = createOperationRecord(commandData.getUser(), commandData.getArgs(), categoryType, session);
        } catch (NotExistingCategoryException e) {
            throw new HandleCommandException(commandData, CATEGORY_DOES_NOT_EXISTS);
        } catch (IllegalArgumentException e) {
            throw new HandleCommandException(commandData, Message.INCORRECT_CATEGORY_ARGUMENT_FORMAT);
        }
        double currentBalance = commandData.getUser().getBalance() + operation.getPayment();
        User user = commandData.getUser();
        user.setBalance(currentBalance);
        userRepository.saveUser(session, user);
        String message = categoryType == CategoryType.INCOME
                ? ADD_INCOME_MESSAGE
                : ADD_EXPENSE_MESSAGE;
        commandData.getSender().sendMessage(user,
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
    private Operation createOperationRecord(User user, List<String> args, CategoryType type, Session session)
            throws NotExistingCategoryException {
        double payment = Double.parseDouble(args.get(0)); // This payment is bigger than 0
        String categoryName = this.categoryParseService.parseCategory(args.subList(1, args.size()));
        if (type == CategoryType.EXPENSE) {
            payment = -payment;
        }
        Optional<Category> category = this.categoryRepository.getCategoryByName(session, user, type, categoryName);
        if (category.isEmpty()) {
            throw new NotExistingCategoryException(categoryName);
        }
        return this.operationRepository.addOperation(session, user, category.get(), payment);
    }
}
