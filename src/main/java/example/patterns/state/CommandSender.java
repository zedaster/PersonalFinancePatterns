package example.patterns.state;

public interface CommandSender {
    void sendMessage(User user, String msg);
}
