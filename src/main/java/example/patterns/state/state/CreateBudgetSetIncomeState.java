package example.patterns.state.state;

import example.patterns.state.CommandSender;
import example.patterns.state.User;
import ru.naumen.personalfinancebot.service.NumberParseService;

import java.time.YearMonth;

public class CreateBudgetSetIncomeState implements UserState {
    private final NumberParseService numberParseService = new NumberParseService();

    private final YearMonth yearMonth;


    public CreateBudgetSetIncomeState(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    @Override
    public void handleText(CommandSender sender, User user, String text) {
        double income;
        try {
            income = this.numberParseService.parsePositiveDouble(text);
        } catch (NumberFormatException e) {
            sender.sendMessage(user, "Доход указан неверно! Введите его еще раз: ");
            return;
        }

        sender.sendMessage(user, "Введите ожидаемый расход: ");
        user.setState(new CreateBudgetSetExpensesState(yearMonth, income));
    }
}
