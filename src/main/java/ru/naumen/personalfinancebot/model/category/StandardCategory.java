package ru.naumen.personalfinancebot.model.category;

import ru.naumen.personalfinancebot.model.CategoryType;

/**
 * Стандартная категория
 */
public class StandardCategory extends SingleCategory {
    public StandardCategory(String name, CategoryType type) {
        super(name, type);
    }
}
