package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.handler.data.CommandMemento;

/**
 * Обработчик команды для бота
 */
// Паттерн Command
public interface CommandHandler {
    /**
     * Метод, вызываемый при получении команды
     * @throws HandleCommandException Если произошла ошибка при исполнении команды
     */
    void handleCommand(CommandData commandData, Session session) throws HandleCommandException;

    /**
     * Метод, вызываемый при отмене команды
     * @param memento Снимок команды
     * @throws UndoCommandException Если произошла ошибка при отмене команды
     */
    default void undoCommand(CommandMemento memento) throws UndoCommandException {
        throw new UndoCommandException(memento, "Эта операция не поддерживается! Обратитесь к разработчикам!");
    }
}
