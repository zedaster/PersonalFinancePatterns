package example.patterns.state.command;

import example.patterns.state.CommandSender;
import example.patterns.state.User;

public interface Command {
    void execute(CommandSender sender, User user);
}
