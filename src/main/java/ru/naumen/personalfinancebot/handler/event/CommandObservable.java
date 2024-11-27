package ru.naumen.personalfinancebot.handler.event;

public interface CommandObservable {
    void addObserver(CommandObserver observer);

    void removeObserver(CommandObserver observer);
}
