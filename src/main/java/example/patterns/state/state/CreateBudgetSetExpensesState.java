package example.patterns.state.state;

import example.patterns.state.CommandSender;
import example.patterns.state.User;
import example.patterns.state.service.BudgetService;
import ru.naumen.personalfinancebot.service.NumberParseService;

import java.time.YearMonth;

public class CreateBudgetSetExpensesState implements UserState {
    private final NumberParseService numberParseService = new NumberParseService();

    private final BudgetService budgetService = BudgetService.getInstance();

    private final YearMonth yearMonth;
    private final double income;

    public CreateBudgetSetExpensesState(YearMonth yearMonth, double income) {
        this.yearMonth = yearMonth;
        this.income = income;
    }

    @Override
    public void handleText(CommandSender sender, User user, String text) {
        double expenses;
        try {
            expenses = this.numberParseService.parsePositiveDouble(text);
        } catch (NumberFormatException e) {
            sender.sendMessage(user, "Расход указан неверно! Введите его еще раз: ");
            return;
        }

        this.budgetService.createBudget(user, yearMonth, income, expenses);
        sender.sendMessage(user, "Бюджет создан!");
        user.setState(WaitingCommandState.getInstance());
    }
}
