package ru.naumen.personalfinancebot.bot;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.model.User;

import java.util.List;

/**
 * Обработчик обновлений в телеграм боте
 */
public interface UpdateHandler {
    /**
     * Вызывается при получении команды в боте
     */
    void onCommandReceived(Session session, User user, String cmdName, List<String> args);
}
