package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.commandData.CommandData;

/**
 * Декоратор, который логирует переданные аргументы в команду
 */
// Паттерн Decorator
public class LoggingDecorator implements CommandHandler {

    private final CommandHandler handler;

    public LoggingDecorator(CommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleCommand(CommandData commandData, Session session) {
        System.out.println("==========================");
        System.out.println("A command is called");
        System.out.println("User id: " + commandData.getUser().getId());
        System.out.println("User chat id: " + commandData.getUser().getChatId());
        System.out.println("User balance: " + commandData.getUser().getBalance());
        System.out.println("Command name: " + commandData.getCommandName());
        System.out.println("Arguments: [" + String.join(", ", commandData.getArgs()
                .stream().map(s -> "\"" + s + "\"").toList()) + "]");
        System.out.println("==========================");

        this.handler.handleCommand(commandData, session);
    }
}
