package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.commandData.CommandData;

import java.util.stream.Stream;

/**
 * Команда, которая может содержать повторяющиеся аргументы
 */
// Паттерн Interpreter
public abstract class MultiCommandHandler implements CommandHandler {
    @Override
    public final void handleCommand(CommandData commandData, Session session) {
        try {
            splitCommandData(commandData).forEach((subCommandData) ->
                    this.handleSingleCommand(subCommandData, session));
        } catch (ArgumentSplitterException e) {
            commandData.getBot().sendMessage(commandData.getUser(), e.getMessage());
        }
    }

    private Stream<CommandData> splitCommandData(CommandData commandData) throws ArgumentSplitterException {
        return this.getArgumentSplitter()
                .splitArguments(commandData.getArgs())
                .stream()
                .map((subArgs) -> {
                    CommandData subData = commandData.copy();
                    subData.setArgs(subArgs);
                    return subData;
                });
    }

    protected abstract ArgumentSplitter getArgumentSplitter();

    protected abstract void handleSingleCommand(CommandData commandData, Session session);
}