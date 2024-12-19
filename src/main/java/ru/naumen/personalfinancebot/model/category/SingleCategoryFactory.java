package ru.naumen.personalfinancebot.model.category;

import ru.naumen.personalfinancebot.model.CategoryType;

// Паттерн Factory Method
public interface SingleCategoryFactory {
    SingleCategory createCategory(CategoryType type, String name);
}