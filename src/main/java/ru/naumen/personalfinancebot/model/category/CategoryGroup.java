package ru.naumen.personalfinancebot.model.category;

import ru.naumen.personalfinancebot.model.CategoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Группа категорий
 */
public class CategoryGroup implements CategoryComponent {
    private final List<CategoryComponent> categoryComponents = new ArrayList<>();

    private final String name;

    private final CategoryType type;

    public CategoryGroup(String name, CategoryType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void add(CategoryComponent categoryComponent) {
        if (categoryComponent.getType() != this.type) {
            throw new IllegalArgumentException("Category type mismatch");
        }
        categoryComponents.add(categoryComponent);
    }

    @Override
    public void remove(CategoryComponent categoryComponent) {
        categoryComponents.remove(categoryComponent);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CategoryType getType() {
        return this.type;
    }

    @Override
    public Stream<CategoryComponent> children() {
        return categoryComponents.stream();
    }
}
