package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.commandData.CommandData;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
        return streamFromIterator(this.splitArguments(commandData.getArgs()))
                .map((subArgs) -> {
                    CommandData subData = commandData.copy();
                    subData.setArgs(subArgs);
                    return subData;
                });
    }

    private <T> Stream<T> streamFromIterator(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }

    protected abstract ArgumentSplitter splitArguments(List<String> args);

    protected abstract void handleSingleCommand(CommandData commandData, Session session);
}