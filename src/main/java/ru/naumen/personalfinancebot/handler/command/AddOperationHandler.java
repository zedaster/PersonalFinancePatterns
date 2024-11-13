package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.commandData.CommandData;
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

import java.util.ArrayList;
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
     * Сообщение о неверно переданном количестве аргументов для команды /add_[income|expense]
     */
    private static final String INCORRECT_OPERATION_ARGS_AMOUNT =
            "Данная команда принимает аргументы: [payment - сумма 1] [категория расхода/дохода 1] [payment - сумма 2] [категория расхода/дохода 2] ...";

    /**
     * Сообщение об отсутствии категории
     */
    private static final String CATEGORY_DOES_NOT_EXISTS =
            "Указанная категория не числится. Используйте команду /add_[income/expense]_category чтобы добавить её";

    /**
     * Сообщение о неверно переданном аргументе, который отвечает за сумму операции
     */
    private static final String INCORRECT_PAYMENT_ARG = "Сумма операции указана в неверном формате. Передайте корректное положительно число";

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
    private final CategoryParseService categoryParseService;


    public AddOperationHandler(CategoryType categoryType, UserRepository userRepository,
                               CategoryRepository categoryRepository, OperationRepository operationRepository,
                               CategoryParseService categoryParseService) {
        this.categoryType = categoryType;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
        this.categoryParseService = categoryParseService;
    }

    @Override
    protected ArgumentSplitter getArgumentSplitter() {
        return (args -> {
           List<List<String>> splitArgs = new ArrayList<>();
           List<String> currentList = null;

            if (args.size() < 2) {
                throw new ArgumentSplitterException(INCORRECT_OPERATION_ARGS_AMOUNT);
            }

           for (String arg : args) {
               try {
                   double sum = Double.parseDouble(arg);
                   if (sum <= 0) {
                       throw new ArgumentSplitterException(INCORRECT_PAYMENT_ARG);
                   }
                   if (currentList != null) {
                       splitArgs.add(currentList);
                   }
                   currentList = new ArrayList<>();
                   currentList.add(String.valueOf(sum));
               } catch (NumberFormatException e) {
                   if (currentList == null) {
                       throw new ArgumentSplitterException(INCORRECT_PAYMENT_ARG);
                   }
                   currentList.add(arg);
               }
           }
            splitArgs.add(currentList);

           return splitArgs;
        });
    }

    @Override
    public void handleSingleCommand(CommandData commandData, Session session) {

        Operation operation;
        try {
            operation = createOperationRecord(commandData.getUser(), commandData.getArgs(), categoryType, session);
        } catch (NotExistingCategoryException e) {
            commandData.getBot().sendMessage(commandData.getUser(), CATEGORY_DOES_NOT_EXISTS);
            return;
        } catch (IllegalArgumentException e) {
            commandData.getBot().sendMessage(commandData.getUser(), Message.INCORRECT_CATEGORY_ARGUMENT_FORMAT);
            return;
        }
        double currentBalance = commandData.getUser().getBalance() + operation.getPayment();
        User user = commandData.getUser();
        user.setBalance(currentBalance);
        userRepository.saveUser(session, user);
        String message = categoryType == CategoryType.INCOME
                ? ADD_INCOME_MESSAGE
                : ADD_EXPENSE_MESSAGE;
        commandData.getBot().sendMessage(user,
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
