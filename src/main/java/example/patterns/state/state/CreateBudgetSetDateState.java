package example.patterns.state.state;

import example.patterns.state.CommandSender;
import example.patterns.state.User;
import ru.naumen.personalfinancebot.service.DateParseService;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class CreateBudgetSetDateState implements UserState {
    private final DateParseService dateParseService = new DateParseService();

    @Override
    public void handleText(CommandSender sender, User user, String text) {
        YearMonth yearMonth;
        try {
            yearMonth = this.dateParseService.parseYearMonth(text);
        } catch (DateTimeParseException e) {
            sender.sendMessage(user, "Неверно введен месяц и год! Введите месяц и год еще раз: ");
            return;
        }

        if (yearMonth.isBefore(YearMonth.now())) {
            sender.sendMessage(user, "");
            sender.sendMessage(user, "Вы не можете создавать бюджеты за прошедшие месяцы! Введите месяц и год еще раз: ");
            return;
        }

        sender.sendMessage(user, "Введите ожидаемый доход за этот месяц: ");
        user.setState(new CreateBudgetSetIncomeState(yearMonth));
    }
}
