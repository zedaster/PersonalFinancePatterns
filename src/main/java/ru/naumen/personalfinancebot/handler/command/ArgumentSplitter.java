package ru.naumen.personalfinancebot.handler.command;

import java.util.List;

@FunctionalInterface
public interface ArgumentSplitter {
    List<List<String>> splitArguments(List<String> args) throws ArgumentSplitterException;
}
