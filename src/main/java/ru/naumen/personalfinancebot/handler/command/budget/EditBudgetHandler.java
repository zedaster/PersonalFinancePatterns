package ru.naumen.personalfinancebot.handler.command.budget;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.command.CommandHandler;
import ru.naumen.personalfinancebot.handler.command.HandleCommandException;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.message.Message;
import ru.naumen.personalfinancebot.model.Budget;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.service.DateParseService;
import ru.naumen.personalfinancebot.service.NumberParseService;
import ru.naumen.personalfinancebot.service.OutputMonthFormatService;
import ru.naumen.personalfinancebot.service.OutputNumberFormatService;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Обработчик для команд "/budget_set_{"expenses" / "income"}"
 */
public class EditBudgetHandler implements CommandHandler {
    /**
     * Сообщение о неверно введенной команде при редактировании бюджета
     */
    private static final String INCORRECT_EDIT_BUDGET_ENTIRE_ARGS = "Неверно введена команда! Введите " +
            "/budget_set_[income/expenses] [mm.yyyy - месяц.год] [ожидаемый доход/расход]";

    /**
     * Сообщение об отсутствии бюджета на указанную пользователем дату
     */
    private static final String BUDGET_NOT_FOUND = "Бюджет на этот период не найден! Создайте его командой " +
            "/budget_create [mm.yyyy - месяц.год] [ожидаемый доход] [ожидаемый расходы]";

    /**
     * Сообщение об ошибке при редактировании бюджета за прошедшие месяцы
     */
    private static final String CANT_EDIT_OLD_BUDGET = "Вы не можете изменять бюджеты за прошедшие месяцы!";

    /**
     * Шаблон сообщения при успешном редактировании бюджета
     */
    private static final String BUDGET_EDITED = """
            Бюджет на %s %s изменен:
            Ожидаемые доходы: %s
            Ожидаемые расходы: %s""";

    /**
     * Репозиторий для работы с бюджетом
     */
    private final BudgetRepository budgetRepository;

    /**
     * Сервис, который парсит числа
     */
    private final NumberParseService numberParseService;

    /**
     * Сервис, который парсит дату
     */
    private final DateParseService dateParseService;

    /**
     * Сервис, который форматирует числа
     */
    private final OutputNumberFormatService numberFormatService;

    /**
     * Сервис, который форматирует месяц к русскому названию
     */
    private final OutputMonthFormatService monthFormatService;

    /**
     * Тип, значение которого нужно изменить в записи бюджета
     */
    private final CategoryType type;

    /**
     * @param budgetRepository    Репозиторий для работы с бюджетом
     * @param numberParseService  Сервис, который парсит числа
     * @param dateParseService    Сервис, который парсит дату
     * @param numberFormatService Сервис, который форматирует числа
     * @param monthFormatService  Сервис, который форматирует месяц к русскому названию
     * @param type                Тип, значение которого нужно изменить в записи бюджета
     */
    public EditBudgetHandler(
            BudgetRepository budgetRepository, NumberParseService numberParseService,
            DateParseService dateParseService, OutputNumberFormatService numberFormatService,
            OutputMonthFormatService monthFormatService, CategoryType type) {
        this.budgetRepository = budgetRepository;
        this.numberParseService = numberParseService;
        this.dateParseService = dateParseService;
        this.numberFormatService = numberFormatService;
        this.monthFormatService = monthFormatService;
        this.type = type;
    }

    @Override
    public void handleCommand(CommandData commandData, Session session) throws HandleCommandException {
        int argsCount = commandData.getArgs().size();
        YearMonth yearMonth = YearMonth.now();
        double amount;
        try {
            if (argsCount == 1) {
                amount = this.numberParseService.parsePositiveDouble(commandData.getArgs().get(0));
            } else if (argsCount == 2) {
                yearMonth = this.dateParseService.parseYearMonth(commandData.getArgs().get(0));
                amount = this.numberParseService.parsePositiveDouble(commandData.getArgs().get(1));
            } else {
                throw new HandleCommandException(commandData, INCORRECT_EDIT_BUDGET_ENTIRE_ARGS);
            }
        } catch (NumberFormatException e) {
            throw new HandleCommandException(commandData, Message.INCORRECT_BUDGET_NUMBER_ARG);
        } catch (DateTimeParseException e) {
            throw new HandleCommandException(commandData, Message.INCORRECT_YEAR_MONTH_FORMAT);
        }

        if (yearMonth.isBefore(YearMonth.now())) {
            throw new HandleCommandException(commandData, CANT_EDIT_OLD_BUDGET);
        }

        Optional<Budget> budget = this.budgetRepository.getBudget(session, commandData.getUser(), yearMonth);
        if (budget.isEmpty()) {
            throw new HandleCommandException(commandData, BUDGET_NOT_FOUND);
        }

        switch (this.type) {
            case INCOME -> budget.get().setIncome(amount);
            case EXPENSE -> budget.get().setExpense(amount);
        }

        this.budgetRepository.saveBudget(session, budget.get());

        double expectIncome = budget.get().getIncome();
        double expectExpenses = budget.get().getExpense();
        commandData.getSender().sendMessage(
                commandData.getUser(),
                BUDGET_EDITED.formatted(
                        monthFormatService.formatRuMonthName(yearMonth.getMonth()),
                        String.valueOf(yearMonth.getYear()),
                        numberFormatService.formatDouble(expectIncome),
                        numberFormatService.formatDouble(expectExpenses)
                )
        );
    }
}
