package ru.naumen.personalfinancebot.handler.commandData;

import ru.naumen.personalfinancebot.bot.Bot;
import ru.naumen.personalfinancebot.model.User;

import java.util.List;

/**
 * Класс для хранения данных о команде, пользователе, аргументах
 */
public class CommandData {
    /**
     * Бот, который обрабатывает команду
     */
    private final Bot bot;

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

    public CommandData(Bot bot, User user, String commandName, List<String> args) {
        this.bot = bot;
        this.user = user;
        this.commandName = commandName;
        this.args = args;
    }

    /**
     * Получает бота, который обрабатывает команду
     */
    public Bot getBot() {
        return bot;
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
        return new CommandData(bot, user, commandName, args);
    }
}
