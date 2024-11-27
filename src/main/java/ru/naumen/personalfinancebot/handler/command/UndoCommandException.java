package ru.naumen.personalfinancebot.handler.command;

import ru.naumen.personalfinancebot.handler.data.CommandMemento;

public class UndoCommandException extends Exception {
    private final CommandMemento commandMemento;

    public UndoCommandException(CommandMemento commandMemento, String message) {
        super(message);
        this.commandMemento = commandMemento;
    }
}
