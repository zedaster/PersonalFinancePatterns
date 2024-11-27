package ru.naumen.personalfinancebot.handler.event;

import ru.naumen.personalfinancebot.handler.data.CommandData;

public class AddCategoryEvent extends CommandEvent {
    private final String categoryName;

    public AddCategoryEvent(CommandData commandData, String categoryName) {
        super(commandData);
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
