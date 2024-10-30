package ru.naumen.personalfinancebot.model.category;

import ru.naumen.personalfinancebot.model.CategoryType;

import java.util.stream.Stream;

/**
 * Лист в дереве категорий
 */
public abstract class SingleCategory implements CategoryComponent {

    private final String name;

    private final CategoryType type;

    public SingleCategory(String name, CategoryType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void add(CategoryComponent categoryComponent) {
        throw new UnsupportedOperationException("Category can't have any children");
    }

    @Override
    public void remove(CategoryComponent categoryComponent) {
        throw new UnsupportedOperationException("Category can't have any children");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public CategoryType getType() {
        return type;
    }

    @Override
    public Stream<CategoryComponent> children() {
        throw new UnsupportedOperationException("Category can't have any children");
    }
}
