package example.patterns.state.state;

import example.patterns.state.CommandSender;
import example.patterns.state.User;

// Паттерн State
public interface UserState {
    void handleText(CommandSender sender, User user, String text);
}
