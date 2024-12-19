package ru.naumen.personalfinancebot.model.category;

import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;

public class UserCategoryFactory implements SingleCategoryFactory {
    private User user;

    public UserCategoryFactory(User user) {
        this.user = user;
    }

    @Override
    public SingleCategory createCategory(CategoryType type, String name) {
        return new UserCategory(name, type, user);
    }

    public void setUser(User user) {
        this.user = user;
    }
}
