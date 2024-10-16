package ru.naumen.personalfinancebot.bot;

import ru.naumen.personalfinancebot.model.User;

public interface TelegramAdapter {
    void startPooling() throws PoolingException;
    void sendMessage(User user, String text);
}
