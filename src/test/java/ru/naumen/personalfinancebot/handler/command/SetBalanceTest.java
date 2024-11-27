package ru.naumen.personalfinancebot.handler.command;

import org.junit.Assert;
import org.junit.Test;
import ru.naumen.personalfinancebot.bot.MockBot;
import ru.naumen.personalfinancebot.bot.MockMessage;
import ru.naumen.personalfinancebot.configuration.HibernateConfiguration;
import ru.naumen.personalfinancebot.handler.FinanceBotHandler;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.budget.HibernateBudgetRepository;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.category.HibernateCategoryRepository;
import ru.naumen.personalfinancebot.repository.operation.HibernateOperationRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.HibernateUserRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Тесты для команды /set_balance в FinanceBotHandler
 */
public class SetBalanceTest {
    /**
     * Хранилище пользователей
     */
    private final UserRepository userRepository;

    /**
     * Обработчик всех команд бота
     */
    private final FinanceBotHandler botHandler;

    /**
     * Менеджер транзакций
     */
    private final TransactionManager transactionManager;

    public SetBalanceTest() {
        HibernateConfiguration hibernateConfiguration = new HibernateConfiguration();
        this.userRepository = new HibernateUserRepository();
        this.transactionManager = new TransactionManager(hibernateConfiguration.getSessionFactory());
        OperationRepository operationRepository = new HibernateOperationRepository();
        CategoryRepository categoryRepository = new HibernateCategoryRepository();
        BudgetRepository budgetRepository = new HibernateBudgetRepository();
        this.botHandler = new FinanceBotHandler(
                userRepository,
                operationRepository,
                categoryRepository,
                budgetRepository);
    }

    /**
     * Выполнение команды без аргументов
     */
    @Test
    public void noArguments() {
        assertIncorrectBalanceCommand(new ArrayList<>());
    }

    /**
     * Выполнение команды с 2 аргументами и более
     */
    @Test
    public void tooManyArguments() {
        List<String> twoArgs = List.of("1000", "2000");
        List<String> thereArgs = List.of("1000", "2000", "3000");
        List<String> hundredArgs = IntStream.range(1, 100)
                .mapToObj(Integer::toString)
                .toList();
        List<List<String>> argsCases = List.of(twoArgs, thereArgs, hundredArgs);

        for (List<String> args : argsCases) {
            assertIncorrectBalanceCommand(args);
        }

    }

    /**
     * Выполнение команды с отрицательным значением баланса
     */
    @Test
    public void negativeBalances() {
        List<String> args = List.of("-1", "-1.0", "-1,0", "-0.0001", "-0,0001");
        for (String arg : args) {
            assertIncorrectBalanceCommand(List.of(arg));
        }
    }

    /**
     * Выполнение команды со значением баланса, у которого цифр после точки более двух
     */
    @Test
    public void tooManyDigitsAfterDotBalances() {
        assertIncorrectBalanceCommand(List.of("100.999"));
    }

    /**
     * Выполнение команды с некорректным значением баланса
     */
    @Test
    public void incorrectBalanceValues() {
        List<String> args = List.of("10e-6", "NaN", "a");
        for (String arg : args) {
            assertIncorrectBalanceCommand(List.of(arg));
        }
    }

    /**
     * Выполнение команды с правильно введенными значениями
     */
    @Test
    public void correctBalance() {
        List<String> args = List.of(
                "100",
                "0",
                "0.0",
                "0.000000",
                "0,0",
                String.valueOf(Integer.MAX_VALUE),
                "100.50",
                "100.99",
                "5000.99"
        );
        List<Double> balances = List.of(
                100d,
                0d,
                0d,
                0d,
                0d,
                (double) Integer.MAX_VALUE,
                100.5,
                100.99,
                5000.99
        );
        List<String> expects = List.of(
                "Ваш баланс изменен. Теперь он составляет 100",
                "Ваш баланс изменен. Теперь он составляет 0",
                "Ваш баланс изменен. Теперь он составляет 0",
                "Ваш баланс изменен. Теперь он составляет 0",
                "Ваш баланс изменен. Теперь он составляет 0",
                "Ваш баланс изменен. Теперь он составляет 2 147 483 647",
                "Ваш баланс изменен. Теперь он составляет 100.5",
                "Ваш баланс изменен. Теперь он составляет 100.99",
                "Ваш баланс изменен. Теперь он составляет 5 000.99"
        );
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            String expect = expects.get(i);
            double balance = balances.get(i);
            assetCorrectBalanceCommand(arg, balance, expect);
        }
    }

    /**
     * Проводит тест с позитивным исходом
     */
    private void assetCorrectBalanceCommand(String argument, double expectedBalance, String expectedMessage) {
        MockBot mockBot = new MockBot();
        User user = new User(123, 12345);
        transactionManager.produceTransaction(session -> {
            userRepository.saveUser(session, user);
            List<String> args = List.of(argument);
            CommandData commandData = new CommandData(mockBot, user, "set_balance", args);
            this.botHandler.handleCommand(commandData, session);

            Assert.assertEquals(1, mockBot.getMessageQueueSize());
            MockMessage message = mockBot.poolMessageQueue();
            Assert.assertEquals(user, message.receiver());
            Assert.assertEquals(expectedMessage, message.text());
            Assert.assertEquals(user.getBalance(), expectedBalance, 1e-15);

            userRepository.removeUserById(session, user.getId());
        });
    }

    /**
     * Проводит тест с отрицательным исходом
     */
    private void assertIncorrectBalanceCommand(List<String> args) {
        transactionManager.produceTransaction(session -> {
            MockBot mockBot = new MockBot();
            User user = new User(123, 12345);
            userRepository.saveUser(session, user);
            CommandData commandData = new CommandData(mockBot, user, "set_balance", args);
            this.botHandler.handleCommand(commandData, session);

            Assert.assertEquals(1, mockBot.getMessageQueueSize());
            MockMessage message = mockBot.poolMessageQueue();
            Assert.assertEquals(user, message.receiver());
            Assert.assertEquals("Команда введена неверно! Введите /set_balance <новый баланс>", message.text());
            Assert.assertEquals(user.getBalance(), 12345, 1e-15);

            userRepository.removeUserById(session, user.getId());
        });
    }
}
