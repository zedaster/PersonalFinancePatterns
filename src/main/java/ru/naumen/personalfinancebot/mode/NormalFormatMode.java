package ru.naumen.personalfinancebot.mode;

import ru.naumen.personalfinancebot.bot.Bot;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.model.User;

import java.util.List;

/**
 * Нормальный режим форматирования
 */
public class NormalFormatMode implements FormatMode {
    @Override
    public CommandData formatCommandData(Bot bot, User user, String cmdName, List<String> args) {
        return new CommandData(bot, user, cmdName, args);
    }

    @Override
    public String formatMessageText(String text) {
        return text;
    }
}
