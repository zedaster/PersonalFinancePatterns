package example.patterns.state;

public class TextHandler {

    public void handleMessage(CommandSender sender, User user, String text) {
        user.getState().handleText(sender, user, text);
    }
}
