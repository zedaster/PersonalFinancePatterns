package ru.naumen.personalfinancebot.bot;

import ru.naumen.personalfinancebot.mode.FormatMode;
import ru.naumen.personalfinancebot.model.User;

/**
 * Описание методов для бота
 */
// Паттерн Bridge
public abstract class Bot {
    /**
     * Режим форматирования бота
     */
    protected final FormatMode formatMode;

    public Bot(FormatMode formatMode) {
        this.formatMode = formatMode;
    }

    /**
     * Запуск бота
     */
    public abstract void startPooling() throws PoolingException;

    /**
     * Отправка текстового сообщения определенному пользователю
     */
    public void sendMessage(User user, String text) {
        String newText = formatMode.formatMessageText(text);
        internalSendMessage(user, newText);
    }

    /**
     * Отправка обработанного текстового сообщения определенному пользователю
     * @param user Пользователь, которому идет отправка
     * @param text Обработанный текст
     */
    protected abstract void internalSendMessage(User user, String text);
}
