package ru.naumen.personalfinancebot.mode;

import ru.naumen.personalfinancebot.bot.Bot;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Режим форматирования в котором слова и числа вводятся и выводятся наоборот.
 */
public class ReverseFormatMode implements FormatMode{
    @Override
    public CommandData formatCommandData(Bot bot, User user, String cmdName, List<String> args) {
        List<String> newArgs = args.stream().map(this::reverseLetters).toList();
        return new CommandData(bot, user, reverseLetters(cmdName), newArgs);
    }

    @Override
    public String formatMessageText(String text) {
        return reverseWords(text);
    }

    /**
     * Переворачивает символы в строке
     * @param str Старая строка
     * @return Новая строка
     */
    private String reverseLetters(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * Переворачивает буквы в словах строки
     * @param text Старый текст
     * @return Новый текст
     */
    private String reverseWords(String text) {
        return Arrays.stream(text.split("\\s+"))
                .map(this::reverseLetters)
                .collect(Collectors.joining(" "));
    }


}
