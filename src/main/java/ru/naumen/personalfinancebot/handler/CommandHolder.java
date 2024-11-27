package ru.naumen.personalfinancebot.handler;

import ru.naumen.personalfinancebot.handler.command.*;
import ru.naumen.personalfinancebot.handler.command.budget.*;
import ru.naumen.personalfinancebot.handler.command.category.AddCategoryHandler;
import ru.naumen.personalfinancebot.handler.command.category.FullListCategoriesHandler;
import ru.naumen.personalfinancebot.handler.command.category.RemoveCategoryHandler;
import ru.naumen.personalfinancebot.handler.command.category.SingleListCategoriesHandler;
import ru.naumen.personalfinancebot.handler.command.operation.AddOperationHandler;
import ru.naumen.personalfinancebot.handler.command.report.AverageReportHandler;
import ru.naumen.personalfinancebot.handler.command.report.EstimateReportHandler;
import ru.naumen.personalfinancebot.handler.command.report.ReportExpensesHandler;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;
import ru.naumen.personalfinancebot.service.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Класс, отвечающий за хранение команд
 */
public class CommandHolder {
    /**
     * Коллекция, которая хранит обработчики для команд
     */
    private final Map<String, CommandHandler> commandHandlers;

    /**
     * Команда для отмены последней команды
     */
    private static final String UNDO_COMMAND_NAME = "undo";

    public CommandHolder(UserRepository userRepository,
                         OperationRepository operationRepository,
                         CategoryRepository categoryRepository,
                         BudgetRepository budgetRepository,
                         CommandHistory commandHistory) {
        DateParseService dateParseService = new DateParseService();
        NumberParseService numberParseService = new NumberParseService();
        OutputNumberFormatService numberFormatService = new OutputNumberFormatService();
        OutputMonthFormatService monthFormatService = new OutputMonthFormatService();
        CategoryListService categoryListService = new CategoryListService(categoryRepository);
        ReportService reportService = new ReportService(operationRepository, monthFormatService, numberFormatService);

        commandHandlers = new HashMap<>();
        commandHandlers.put("start", new StartCommandHandler());
        commandHandlers.put("set_balance", new SetBalanceHandler(numberParseService, numberFormatService,
                userRepository));
        commandHandlers.put("add_expense", new AddOperationHandler(CategoryType.EXPENSE, userRepository,
                categoryRepository, operationRepository));
        commandHandlers.put("add_income", new AddOperationHandler(CategoryType.INCOME, userRepository,
                categoryRepository, operationRepository));
        commandHandlers.put("add_income_category", new AddCategoryHandler(CategoryType.INCOME, categoryRepository));
        commandHandlers.put("add_expense_category", new AddCategoryHandler(CategoryType.EXPENSE, categoryRepository));
        commandHandlers.put("remove_income_category", new RemoveCategoryHandler(CategoryType.INCOME,
                categoryRepository));
        commandHandlers.put("remove_expense_category", new RemoveCategoryHandler(CategoryType.EXPENSE,
                categoryRepository));
        commandHandlers.put("list_categories", new FullListCategoriesHandler(categoryListService));
        commandHandlers.put("list_income_categories", new SingleListCategoriesHandler(CategoryType.INCOME,
                categoryListService));
        commandHandlers.put("list_expense_categories", new SingleListCategoriesHandler(CategoryType.EXPENSE,
                categoryListService));
        commandHandlers.put("report_expense", new ReportExpensesHandler(reportService));

        commandHandlers.put("budget", new SingleBudgetHandler(budgetRepository, operationRepository,
                numberFormatService, monthFormatService));
        commandHandlers.put("budget_help", new HelpBudgetHandler());
        commandHandlers.put("budget_create", new CreateBudgetHandler(budgetRepository, operationRepository));
        commandHandlers.put("budget_set_income", new EditBudgetHandler(budgetRepository, numberParseService,
                dateParseService, numberFormatService, monthFormatService, CategoryType.INCOME));
        commandHandlers.put("budget_set_expenses", new EditBudgetHandler(budgetRepository, numberParseService,
                dateParseService, numberFormatService, monthFormatService, CategoryType.EXPENSE));
        commandHandlers.put("budget_list", new ListBudgetHandler(budgetRepository, operationRepository,
                dateParseService, numberFormatService, monthFormatService));

        commandHandlers.put("estimate_report", new LoggingDecorator(new EstimateReportHandler(dateParseService, reportService)));
        commandHandlers.put("avg_report", new LoggingDecorator(new AverageReportHandler(dateParseService, reportService)));

        commandHandlers.put(UNDO_COMMAND_NAME, new UndoCommandHandler(this, commandHistory));
    }

    public Optional<CommandHandler> getCommandHandler(String commandName) {
        return Optional.ofNullable(this.commandHandlers.get(commandName.toLowerCase()));
    }

    public boolean isUndoCommand(String commandName) {
        return commandName.equalsIgnoreCase(UNDO_COMMAND_NAME);
    }
}
