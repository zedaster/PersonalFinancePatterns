package example.patterns.state;

import java.util.Scanner;

public class ConsoleWorker implements CommandSender {
    public ConsoleWorker(TextHandler textHandler) {
        Scanner scanner = new Scanner(System.in);
        User consoleUser = new User("console");
        Thread thread = new Thread(() -> {
            while (true) {
                String newLine = scanner.nextLine();
                textHandler.handleMessage(this, consoleUser, newLine);
            }
        });
        thread.start();
        this.sendMessage(consoleUser, "Добро пожаловать! Введите команду: ");
    }

    @Override
    public void sendMessage(User user, String msg) {
        System.out.println(msg);
    }
}
