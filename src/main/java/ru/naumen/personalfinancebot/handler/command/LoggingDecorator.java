package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.handler.data.CommandMemento;

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
    public void handleCommand(CommandData commandData, Session session) throws HandleCommandException {
        System.out.println("==========================");
        System.out.println("A command is called");
        System.out.println("User id: " + commandData.getUser().getId());
        System.out.println("User chat id: " + commandData.getUser().getChatId());
        System.out.println("User balance: " + commandData.getUser().getBalance());
        System.out.println("Command name: " + commandData.getCommandName());
        System.out.println("Arguments: [" + String.join(", ", commandData.getArgs()
                .stream().map(s -> "\"" + s + "\"").toList()) + "]");
        System.out.println("==========================");

        try {
            this.handler.handleCommand(commandData, session);
        } catch (HandleCommandException e) {
            System.out.println("==========================");
            System.out.println("An error was occurred in a command");
            System.out.println("User id: " + commandData.getUser().getId());
            System.out.println("User chat id: " + commandData.getUser().getChatId());
            System.out.println("User balance: " + commandData.getUser().getBalance());
            System.out.println("Command name: " + commandData.getCommandName());
            System.out.println("Arguments: [" + String.join(", ", commandData.getArgs()
                    .stream().map(s -> "\"" + s + "\"").toList()) + "]");
            System.out.println("Error message: " + e.getMessage());
            System.out.println("==========================");
            throw e;
        }

    }

    @Override
    public void undoCommand(CommandMemento memento) throws UndoCommandException {
        System.out.println("==========================");
        System.out.println("A command is undoing");
        System.out.println("User id: " + memento.getUser().getId());
        System.out.println("User chat id: " + memento.getUser().getChatId());
        System.out.println("User balance: " + memento.getUser().getBalance());
        System.out.println("Command name: " + memento.getCommandName());
        System.out.println("Arguments: [" + String.join(", ", memento.getArgs()
                .stream().map(s -> "\"" + s + "\"").toList()) + "]");
        System.out.println("==========================");

        try {
            this.handler.undoCommand(memento);
        } catch (UndoCommandException e) {
            System.out.println("==========================");
            System.out.println("An error was occurred in a command undoing");
            System.out.println("User id: " + memento.getUser().getId());
            System.out.println("User chat id: " + memento.getUser().getChatId());
            System.out.println("User balance: " + memento.getUser().getBalance());
            System.out.println("Command name: " + memento.getCommandName());
            System.out.println("Arguments: [" + String.join(", ", memento.getArgs()
                    .stream().map(s -> "\"" + s + "\"").toList()) + "]");
            System.out.println("Error message: " + e.getMessage());
            System.out.println("==========================");
            throw e;
        }

    }
}
