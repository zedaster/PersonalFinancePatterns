package ru.naumen.personalfinancebot.handler.command;

import java.util.Iterator;
import java.util.List;

public abstract class ArgumentSplitter implements Iterator<List<String>> {
    protected final List<String> arguments;

    public ArgumentSplitter(List<String> arguments) {
        this.arguments = arguments;
    }
}
