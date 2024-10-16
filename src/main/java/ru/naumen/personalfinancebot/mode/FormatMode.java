package ru.naumen.personalfinancebot.mode;

import ru.naumen.personalfinancebot.bot.Bot;
import ru.naumen.personalfinancebot.handler.commandData.CommandData;
import ru.naumen.personalfinancebot.model.User;

import java.util.List;

/**
 * Режим форматирования в боте
 */
public interface FormatMode {
    /**
     * Форматирует данные для команды
     * @param bot Бот для команды
     * @param user Пользователь
     * @param cmdName Название команды
     * @param args Аргументы для команды
     * @return Отформатированные данные для команды
     */
    CommandData formatCommandData(Bot bot, User user, String cmdName, List<String> args);

    /**
     * Форматирует сообщения в боте
     * @param text Текст сообщение
     * @return Отформатированный текст сообщения
     */
    String formatMessageText(String text);
}
