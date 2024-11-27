package ru.naumen.personalfinancebot.handler;

import ru.naumen.personalfinancebot.handler.data.CommandMemento;
import ru.naumen.personalfinancebot.model.User;

import java.util.*;

public class CommandHistory {
    private final Map<User, Deque<CommandMemento>> history = new HashMap<>();
    public void add(CommandMemento memento) {
        if (!history.containsKey(memento.getUser())) {
            history.put(memento.getUser(), new LinkedList<>());
        }
        history.get(memento.getUser()).addLast(memento);
    }

    public Optional<CommandMemento> pollLastCommand(User user) {
        if (!history.containsKey(user)) {
            return Optional.empty();
        }
        return Optional.ofNullable(history.get(user).pollLast());
    }
}
