package example.patterns.state.state;

import example.patterns.state.CommandSender;
import example.patterns.state.User;
import example.patterns.state.command.Command;
import example.patterns.state.command.CommandHolder;

import java.util.Optional;

public class WaitingCommandState implements UserState {
    private static final WaitingCommandState instance = new WaitingCommandState();

    private final CommandHolder commandHolder = CommandHolder.getInstance();

    private WaitingCommandState() {

    }

    public static WaitingCommandState getInstance() {
        return instance;
    }

    @Override
    public void handleText(CommandSender sender, User user, String text) {
        if (!text.startsWith("/")) {
            sender.sendMessage(user, "Команда должна начинаться с \"/\". Введите команду еще раз: ");
            return;
        }
        Optional<Command> command = this.commandHolder.getCommand(text.substring(1));
        if (command.isEmpty()) {
            sender.sendMessage(user, "Такая команда не найдена! Введите команду еще раз: ");
            return;
        }
        command.get().execute(sender, user);
    }
}
