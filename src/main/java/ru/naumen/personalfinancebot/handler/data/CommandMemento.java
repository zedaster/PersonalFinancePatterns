package ru.naumen.personalfinancebot.handler.data;

import ru.naumen.personalfinancebot.model.User;

import java.util.List;

// Паттерн Memento
/**
 * Снимок выполненной команды
 */
public class CommandMemento {
    /**
     * Пользователь, который отправил команду
     */
    private final User user;

    /**
     * Имя команды
     */
    private final String commandName;

    /**
     * Список аргументов к команде
     */
    private final List<String> args;

    public CommandMemento(User user, String commandName, List<String> args) {
        this.user = user;
        this.commandName = commandName;
        this.args = args;
    }

    public User getUser() {
        return user;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getArgs() {
        return args;
    }
}
