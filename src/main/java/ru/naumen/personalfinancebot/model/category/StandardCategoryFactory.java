package ru.naumen.personalfinancebot.model.category;

import ru.naumen.personalfinancebot.model.CategoryType;

public class StandardCategoryFactory implements SingleCategoryFactory {
    @Override
    public SingleCategory createCategory(CategoryType type, String name) {
        return new StandardCategory(name, type);
    }
}
