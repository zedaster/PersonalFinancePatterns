package ru.naumen.personalfinancebot.model.category;

import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;

/**
 * Пользовательская категория
 */
public class UserCategory extends SingleCategory {
    private final User user;

    public UserCategory(String name, CategoryType type, User user) {
        super(name, type);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
