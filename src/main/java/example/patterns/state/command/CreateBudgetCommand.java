package example.patterns.state.command;

import example.patterns.state.CommandSender;
import example.patterns.state.User;
import example.patterns.state.state.CreateBudgetSetDateState;

public class CreateBudgetCommand implements Command {
    @Override
    public void execute(CommandSender sender, User user) {
        sender.sendMessage(user, "Введите месяц и год:");
        user.setState(new CreateBudgetSetDateState());
    }
}
