package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.CommandHistory;
import ru.naumen.personalfinancebot.handler.CommandHolder;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.handler.data.CommandMemento;

import java.util.Optional;

/**
 * Обработчик команды для отмены предыдущей успешной команды
 */
public class UndoCommandHandler implements CommandHandler {
    private final CommandHistory commandHistory;

    private final CommandHolder commandHolder;

    public UndoCommandHandler(CommandHolder commandHolder, CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
        this.commandHolder = commandHolder;
    }

    @Override
    public void handleCommand(CommandData commandData, Session session) throws HandleCommandException {
        Optional<CommandMemento> memento = commandHistory.pollLastCommand(commandData.getUser());
        if (memento.isEmpty()) {
            throw new HandleCommandException(commandData, "У вас нет последних исполненных команд");
        }

        Optional<CommandHandler> handler = this.commandHolder.getCommandHandler(memento.get().getCommandName());
        if (handler.isEmpty()) {
            throw new HandleCommandException(commandData, "Команда для отмены не найдена!");
        }

        try {
            handler.get().undoCommand(memento.get());
        } catch (UndoCommandException e) {
            throw new HandleCommandException(commandData, e.getMessage());
        }
    }

    @Override
    public void undoCommand(CommandMemento commandMemento) throws UndoCommandException {
        throw new UndoCommandException(commandMemento, "Нельзя отменить команду отмены!");
    }
}
