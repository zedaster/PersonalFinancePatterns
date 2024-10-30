package ru.naumen.personalfinancebot.handler.command.budget;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.command.CommandHandler;
import ru.naumen.personalfinancebot.handler.commandData.CommandData;
import ru.naumen.personalfinancebot.handler.validator.ArgumentValidator;
import ru.naumen.personalfinancebot.handler.validator.ArgumentValidatorException;
import ru.naumen.personalfinancebot.message.Message;
import ru.naumen.personalfinancebot.model.Budget;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.service.DateParseService;
import ru.naumen.personalfinancebot.service.NumberParseService;
import ru.naumen.personalfinancebot.service.OutputMonthFormatService;
import ru.naumen.personalfinancebot.service.OutputNumberFormatService;

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
    private static final String BUDGET_CREATED = """
            Бюджет на %s %s создан.
            Ожидаемые доходы: %s
            Ожидаемые расходы: %s
            Текущие доходы: %s
            Текущие расходы: %s
            Текущий баланс: %s
            Нужно еще заработать: %s
            Еще осталось на траты: %s""";

    /**
     * Репозиторий для работы с бюджетом
     */
    private final BudgetRepository budgetRepository;

    /**
     * Репозиторий для работы с операциями
     */
    private final OperationRepository operationRepository;

    /**
     * Сервис, который форматирует числа
     */
    private final OutputNumberFormatService numberFormatService;

    /**
     * Сервис, который форматирует месяц к русскому названию
     */
    private final OutputMonthFormatService monthFormatService;


    /**
     * @param budgetRepository    Репозиторий для работы с бюджетом
     * @param operationRepository Репозиторий для работы с операциями
     * @param numberFormatService Сервис, который форматирует числа
     * @param monthFormatService  Сервис, который форматирует месяц к русскому названию
     */
    public CreateBudgetHandler(BudgetRepository budgetRepository, OperationRepository operationRepository,
                               OutputNumberFormatService numberFormatService, OutputMonthFormatService monthFormatService) {
        this.budgetRepository = budgetRepository;
        this.operationRepository = operationRepository;
        this.numberFormatService = numberFormatService;
        this.monthFormatService = monthFormatService;
    }

    @Override
    public void handleCommand(CommandData commandData, Session session) {
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
            commandData.getBot().sendMessage(commandData.getUser(), e.getInvalidMessage());
            return;
        }

        if (yearMonth.isBefore(YearMonth.now())) {
            commandData.getBot().sendMessage(commandData.getUser(), CANT_CREATE_OLD_BUDGET);
            return;
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

        commandData.getBot().sendMessage(user,
                BUDGET_CREATED.formatted(
                        monthFormatService.formatRuMonthName(yearMonth.getMonth()),
                        String.valueOf(yearMonth.getYear()),
                        numberFormatService.formatDouble(expectedIncome),
                        numberFormatService.formatDouble(expectedExpenses),
                        numberFormatService.formatDouble(currentIncome),
                        numberFormatService.formatDouble(currentExpenses),
                        numberFormatService.formatDouble(balance),
                        numberFormatService.formatDouble(incomeLeft),
                        numberFormatService.formatDouble(expensesLeft)
                )
        );
    }
}
