package ru.naumen.personalfinancebot.repository;

import ru.naumen.personalfinancebot.model.Category;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.List;

/**
 * Абстрактная фабрика для создания репозиториев
 */
// Паттерн Abstract Factory
public interface RepositoryFactory {

    /**
     * Создает репозиторий для работы с пользователями
     */
    UserRepository newUserRepository();

    /**
     * Создает репозиторий для работы с операциями
     */
    OperationRepository newOperationRepository();

    /**
     * Создает репозиторий для работы с категориями
     */
    CategoryRepository newCategoryRepository(List<Category> standardCategories);

    /**
     * Создает репозиторий для работы с бюджетами
     */
    BudgetRepository newBudgetRepository();
}
