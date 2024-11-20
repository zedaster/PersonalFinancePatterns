package ru.naumen.personalfinancebot.handler;

import ru.naumen.personalfinancebot.model.User;

public interface CommandSender {
    void sendMessage(User user, String message);
}
