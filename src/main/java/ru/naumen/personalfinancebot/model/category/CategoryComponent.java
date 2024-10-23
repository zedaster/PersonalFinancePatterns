package ru.naumen.personalfinancebot.model.category;

import ru.naumen.personalfinancebot.model.CategoryType;

import java.util.stream.Stream;

/**
 * Элемент дерева категорий
 */
public interface CategoryComponent {
    void add(CategoryComponent categoryComponent);

    void remove(CategoryComponent categoryComponent);

    String getName();

    CategoryType getType();

    Stream<CategoryComponent> children();

}
