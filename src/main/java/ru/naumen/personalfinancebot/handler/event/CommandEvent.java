package ru.naumen.personalfinancebot.handler.event;

import ru.naumen.personalfinancebot.handler.data.CommandData;

public abstract class CommandEvent {
    private final CommandData commandData;

    public CommandEvent(CommandData commandData) {
        this.commandData = commandData;
    }

    public CommandData getCommandData() {
        return commandData;
    }
}
