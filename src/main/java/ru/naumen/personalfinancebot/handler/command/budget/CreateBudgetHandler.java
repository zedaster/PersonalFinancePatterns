package ru.naumen.personalfinancebot.handler.command.budget;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.command.CommandHandler;
import ru.naumen.personalfinancebot.handler.command.HandleCommandException;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.handler.validator.ArgumentValidator;
import ru.naumen.personalfinancebot.handler.validator.ArgumentValidatorException;
import ru.naumen.personalfinancebot.message.Message;
import ru.naumen.personalfinancebot.message.format.MessageBuilder;
import ru.naumen.personalfinancebot.model.Budget;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;

import java.time.YearMonth;

/**
 * Обработчик команды "/budget_create".
 */
public class CreateBudgetHandler implements CommandHandler {
    /**
     * Сообщение о неверно введенной команде /budget_create
     */
    private static final String INCORRECT_CREATE_BUDGET_ENTIRE_ARGS = "Неверно введена команда! Введите " +
                                                                      "/budget_create [mm.yyyy - месяц.год] [ожидаемый доход] [ожидаемый расходы]";

    /**
     * Сообщение об ошибке в случае если пользователь планирует бюджет на прошлое.
     */
    private static final String CANT_CREATE_OLD_BUDGET = "Вы не можете создавать бюджеты за прошедшие месяцы!";

    /**
     * Шаблон сообщения для вывода сообщения о созданном бюджете
     */
    private static final MessageBuilder BUDGET_CREATED = new MessageBuilder("""
            Бюджет на %s %s создан.
            Ожидаемые доходы: %s
            Ожидаемые расходы: %s
            Текущие доходы: %s
            Текущие расходы: %s
            Текущий баланс: %s
            Нужно еще заработать: %s
            Еще осталось на траты: %s""");

    /**
     * Репозиторий для работы с бюджетом
     */
    private final BudgetRepository budgetRepository;

    /**
     * Репозиторий для работы с операциями
     */
    private final OperationRepository operationRepository;

    /**
     * @param budgetRepository    Репозиторий для работы с бюджетом
     * @param operationRepository Репозиторий для работы с операциями
     */
    public CreateBudgetHandler(BudgetRepository budgetRepository, OperationRepository operationRepository) {
        this.budgetRepository = budgetRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public void handleCommand(CommandData commandData, Session session) throws HandleCommandException {
        YearMonth yearMonth;
        double expectedIncome;
        double expectedExpenses;

        ArgumentValidator validator = new ArgumentValidator(commandData.getArgs());
        try {
            validator.validateLength(3, INCORRECT_CREATE_BUDGET_ENTIRE_ARGS);
            yearMonth = validator.parseNextValidYearMonth(Message.INCORRECT_YEAR_MONTH_FORMAT);
            expectedIncome = validator.parseNextValidPositiveDouble(Message.INCORRECT_BUDGET_NUMBER_ARG);
            expectedExpenses = validator.parseNextValidPositiveDouble(Message.INCORRECT_BUDGET_NUMBER_ARG);
        } catch (ArgumentValidatorException e) {
            throw new HandleCommandException(commandData, e.getInvalidMessage());
        }

        if (yearMonth.isBefore(YearMonth.now())) {
            throw new HandleCommandException(commandData, CANT_CREATE_OLD_BUDGET);
        }

        User user = commandData.getUser();
        double balance = user.getBalance();
        double currentIncome = this.operationRepository.getCurrentUserPaymentSummary(session, user, CategoryType.INCOME, yearMonth);
        double currentExpenses = this.operationRepository.getCurrentUserPaymentSummary(session, user, CategoryType.EXPENSE, yearMonth);
        double incomeLeft = expectedIncome - currentIncome;
        double expensesLeft = expectedExpenses - currentExpenses;

        Budget budget = new Budget();
        budget.setIncome(expectedIncome);
        budget.setExpense(expectedExpenses);
        budget.setTargetDate(yearMonth);
        budget.setUser(user);
        budgetRepository.saveBudget(session, budget);

        commandData.getSender().sendMessage(user,
                BUDGET_CREATED
                        .nextRuMonth(yearMonth.getMonth())
                        .nextInteger(yearMonth.getYear())
                        .nextDouble(expectedIncome)
                        .nextDouble(expectedExpenses)
                        .nextDouble(currentIncome)
                        .nextDouble(currentExpenses)
                        .nextDouble(balance)
                        .nextDouble(incomeLeft)
                        .nextDouble(expensesLeft)
                        .buildString()
        );
    }
}
