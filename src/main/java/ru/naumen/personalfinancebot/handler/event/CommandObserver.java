package ru.naumen.personalfinancebot.handler.event;

public interface CommandObserver {
    void handleEvent(CommandEvent event);
}
