package me.zedaster.financeadminui.frame;

import ru.naumen.personalfinancebot.handler.event.AddCategoryEvent;
import ru.naumen.personalfinancebot.handler.event.CommandEvent;
import ru.naumen.personalfinancebot.handler.event.CommandObserver;

// Паттерн Observer
public class AddCategoryNotificator implements CommandObserver {
    @Override
    public void handleEvent(CommandEvent event) {
        if (event instanceof AddCategoryEvent) {
            event.getCommandData().getSender().sendMessage(
                    event.getCommandData().getUser(),
                    "Вам добавлена новая категория: %s".formatted(((AddCategoryEvent) event).getCategoryName()));
        }
    }
}
