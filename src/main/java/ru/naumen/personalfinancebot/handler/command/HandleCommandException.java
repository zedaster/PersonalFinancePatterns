package ru.naumen.personalfinancebot.handler.command;

import ru.naumen.personalfinancebot.handler.data.CommandData;

public class HandleCommandException extends Exception {
    private final CommandData commandData;

    public HandleCommandException(CommandData commandData, String message) {
        super(message);
        this.commandData = commandData;
    }

    public CommandData getCommandData() {
        return commandData;
    }
}
