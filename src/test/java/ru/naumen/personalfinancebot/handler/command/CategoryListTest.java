package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.naumen.personalfinancebot.bot.MockBot;
import ru.naumen.personalfinancebot.bot.MockMessage;
import ru.naumen.personalfinancebot.configuration.HibernateConfiguration;
import ru.naumen.personalfinancebot.handler.FinanceBotHandler;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.model.Category;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.ClearQueryManager;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.budget.HibernateBudgetRepository;
import ru.naumen.personalfinancebot.repository.category.HibernateCategoryRepository;
import ru.naumen.personalfinancebot.repository.category.exception.ExistingStandardCategoryException;
import ru.naumen.personalfinancebot.repository.category.exception.ExistingUserCategoryException;
import ru.naumen.personalfinancebot.repository.operation.HibernateOperationRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.HibernateUserRepository;

import java.util.List;

/**
 * Тесты на команды для вывода категорий
 */
public class CategoryListTest {
    /**
     * Хранилище пользователей
     */
    private final HibernateUserRepository userRepository;

    /**
     * Хранилище категорий
     * Данная реализация позволяет сделать полную очистку категорий после тестов
     */
    private final HibernateCategoryRepository categoryRepository;

    /**
     * Обработчик команд для бота
     */
    private final FinanceBotHandler botHandler;

    /**
     * Моковый пользователь. Пересоздается для каждого теста
     */
    private final User mockUser;

    /**
     * Менеджер транзакций
     */
    private final TransactionManager transactionManager;

    /**
     * Моковый бот. Пересоздается для каждого теста.
     */
    private MockBot mockBot;

    public CategoryListTest() {
        SessionFactory sessionFactory = new HibernateConfiguration().getSessionFactory();
        this.userRepository = new HibernateUserRepository();
        this.categoryRepository = new HibernateCategoryRepository();
        OperationRepository operationRepository = new HibernateOperationRepository();
        BudgetRepository budgetRepository = new HibernateBudgetRepository();
        this.botHandler = new FinanceBotHandler(userRepository, operationRepository, categoryRepository, budgetRepository);
        this.transactionManager = new TransactionManager(sessionFactory);

        this.mockUser = new User(1L, 100);
        transactionManager.produceTransaction(session -> this.userRepository.saveUser(session, this.mockUser));

    }

    /**
     * Создаем пользователя и бота перед каждым тестом
     */
    @Before
    public void beforeEachTest() {
        this.mockBot = new MockBot();

        transactionManager.produceTransaction(session -> {
            // Наполняем стандартные категории перед тестами
            try {
                categoryRepository.createStandardCategory(session, CategoryType.INCOME, "Standard income 1");
                categoryRepository.createStandardCategory(session, CategoryType.INCOME, "Standard income 2");
                categoryRepository.createStandardCategory(session, CategoryType.EXPENSE, "Standard expense 1");
                categoryRepository.createStandardCategory(session, CategoryType.EXPENSE, "Standard expense 2");
            } catch (ExistingStandardCategoryException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Удаляем всех пользователей из БД после каждого теста
     */
    @After
    public void afterEachTest() {
        transactionManager.produceTransaction(session -> {
            new ClearQueryManager().clear(session, Category.class, User.class);
        });
    }

    /**
     * Тестирует отображение доходов+расходов или доходов или расходов нескольких (трех) категорий доходов и
     * нескольких (трех) категорий расходов.
     */
    @Test
    public void showCoupleOfCategories() {
        final String expectFullMsg = """
                Все доступные вам категории доходов:
                Стандартные:
                1. Standard income 1
                2. Standard income 2
                                
                Персональные:
                1. Personal income 1
                2. Personal income 2
                3. Personal income 3
                                
                Все доступные вам категории расходов:
                Стандартные:
                1. Standard expense 1
                2. Standard expense 2
                                
                Персональные:
                1. Personal expense 1
                2. Personal expense 2
                3. Personal expense 3
                """;

        final String expectIncomeMsg = """
                Все доступные вам категории доходов:
                Стандартные:
                1. Standard income 1
                2. Standard income 2
                                
                Персональные:
                1. Personal income 1
                2. Personal income 2
                3. Personal income 3
                """;

        final String expectExpensesMsg = """
                Все доступные вам категории расходов:
                Стандартные:
                1. Standard expense 1
                2. Standard expense 2
                                
                Персональные:
                1. Personal expense 1
                2. Personal expense 2
                3. Personal expense 3
                """;

        transactionManager.produceTransaction(session -> {
            try {
                addUserCategories(session, this.mockUser, CategoryType.INCOME, "Personal income 1", "Personal income 2",
                        "Personal income 3");
                addUserCategories(session, this.mockUser, CategoryType.EXPENSE, "Personal expense 1", "Personal expense 2",
                        "Personal expense 3");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final List<String> commands = List.of("list_categories", "list_income_categories", "list_expense_categories");
            for (String commandName : commands) {
                CommandData allCommand = new CommandData(
                        this.mockBot,
                        this.mockUser,
                        commandName,
                        List.of());
                this.botHandler.handleCommand(allCommand, session);
            }

            Assert.assertEquals(3, this.mockBot.getMessageQueueSize());
            Assert.assertEquals(expectFullMsg, this.mockBot.poolMessageQueue().text());
            Assert.assertEquals(expectIncomeMsg, this.mockBot.poolMessageQueue().text());
            Assert.assertEquals(expectExpensesMsg, this.mockBot.poolMessageQueue().text());
        });
    }

    /**
     * Тестирует отображение по 1 категории на доход и расход у пользователя
     */
    @Test
    public void showOneCategory() {
        final String expectMsg = """
                Все доступные вам категории доходов:
                Стандартные:
                1. Standard income 1
                2. Standard income 2
                                
                Персональные:
                1. Personal income 1
                                
                Все доступные вам категории расходов:
                Стандартные:
                1. Standard expense 1
                2. Standard expense 2
                                
                Персональные:
                1. Personal expense 1
                """;
        transactionManager.produceTransaction(session -> {
            try {
                addUserCategories(session, this.mockUser, CategoryType.INCOME, "Personal income 1");
                addUserCategories(session, this.mockUser, CategoryType.EXPENSE, "Personal expense 1");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            CommandData command = new CommandData(this.mockBot, this.mockUser, "list_categories",
                    List.of());
            this.botHandler.handleCommand(command, session);
            Assert.assertEquals(1, this.mockBot.getMessageQueueSize());
            MockMessage lastMessage = this.mockBot.poolMessageQueue();
            Assert.assertEquals(expectMsg, lastMessage.text());
        });
    }

    /**
     * Тестирует отображение пользовательских категорий при их отсутствии
     */
    @Test
    public void showNoCategories() {
        final String expectMsg = """
                Все доступные вам категории доходов:
                Стандартные:
                1. Standard income 1
                2. Standard income 2
                                
                Персональные:
                <отсутствуют>
                                
                Все доступные вам категории расходов:
                Стандартные:
                1. Standard expense 1
                2. Standard expense 2
                                
                Персональные:
                <отсутствуют>
                """;

        transactionManager.produceTransaction(session -> {
            CommandData command = new CommandData(this.mockBot, this.mockUser, "list_categories",
                    List.of());
            this.botHandler.handleCommand(command, session);
            Assert.assertEquals(1, this.mockBot.getMessageQueueSize());
            MockMessage lastMessage = this.mockBot.poolMessageQueue();
            Assert.assertEquals(expectMsg, lastMessage.text());
        });
    }

    /**
     * Проверяет, отобразятся ли категории одного пользователя у другого
     */
    @Test
    public void privacyOfPersonalCategories() {
        final String expectMockUserMsg = """
                Все доступные вам категории доходов:
                Стандартные:
                1. Standard income 1
                2. Standard income 2
                                
                Персональные:
                1. Personal income 1
                2. Personal income 2
                3. Personal income 3
                                
                Все доступные вам категории расходов:
                Стандартные:
                1. Standard expense 1
                2. Standard expense 2
                                
                Персональные:
                <отсутствуют>
                """;

        final String expectSecondUserMsg = """
                Все доступные вам категории доходов:
                Стандартные:
                1. Standard income 1
                2. Standard income 2
                                
                Персональные:
                <отсутствуют>
                                
                Все доступные вам категории расходов:
                Стандартные:
                1. Standard expense 1
                2. Standard expense 2
                                
                Персональные:
                <отсутствуют>
                """;

        transactionManager.produceTransaction(session -> {
            User secondUser = new User(2L, 200.0);
            userRepository.saveUser(session, secondUser);

            try {
                addUserCategories(session, this.mockUser, CategoryType.INCOME, "Personal income 1", "Personal income 2",
                        "Personal income 3");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<User> users = List.of(this.mockUser, secondUser);
            List<String> expectMessages = List.of(expectMockUserMsg, expectSecondUserMsg);
            for (int i = 0; i < 2; i++) {
                CommandData command = new CommandData(
                        this.mockBot,
                        users.get(i),
                        "list_categories",
                        List.of());
                this.botHandler.handleCommand(command, session);

                Assert.assertEquals(1, this.mockBot.getMessageQueueSize());
                MockMessage lastMessage = this.mockBot.poolMessageQueue();
                Assert.assertEquals(users.get(i), lastMessage.receiver());
                Assert.assertEquals(expectMessages.get(i), lastMessage.text());
            }
        });
    }

    /**
     * Метод добавляет несколько пользовательских категорий определенного типа сразу
     *
     * @param session Сессия
     * @param user    Пользователь
     * @param type    Типа категории
     * @param names   Имена для новых категорий
     * @throws ExistingUserCategoryException     если какая-то из категорий уже существует как пользовательская
     * @throws ExistingStandardCategoryException если какая-то из категорий уже существует как стандартная
     */
    private void addUserCategories(Session session, User user, CategoryType type, String... names) throws
            ExistingUserCategoryException,
            ExistingStandardCategoryException {
        for (String name : names) {
            this.categoryRepository.createUserCategory(session, user, type, name);
        }
    }
}
