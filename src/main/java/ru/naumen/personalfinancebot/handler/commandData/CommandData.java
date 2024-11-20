package ru.naumen.personalfinancebot.handler.commandData;

import ru.naumen.personalfinancebot.handler.CommandSender;
import ru.naumen.personalfinancebot.model.User;

import java.util.List;

/**
 * Класс для хранения данных о команде, пользователе, аргументах
 */
public class CommandData {
    /**
     * Бот, который обрабатывает команду
     */
    private final CommandSender sender;

    /**
     * Пользователь, который отправил команду
     */
    private final User user;

    /**
     * Название команды
     */
    private final String commandName;

    /**
     * Список аргументов к команде
     */
    private List<String> args;

    public CommandData(CommandSender sender, User user, String commandName, List<String> args) {
        this.sender = sender;
        this.user = user;
        this.commandName = commandName;
        this.args = args;
    }

    /**
     * Получает отправителя, который обрабатывает команду
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Получает пользователя, который отправил команду
     */
    public User getUser() {
        return user;
    }

    /**
     * Получает название команды
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Получает список аргументов к команде
     */
    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public CommandData copy() {
        return new CommandData(sender, user, commandName, args);
    }
}
