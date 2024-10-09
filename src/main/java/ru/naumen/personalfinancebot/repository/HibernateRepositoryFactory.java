package ru.naumen.personalfinancebot.repository;

import ru.naumen.personalfinancebot.model.Category;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.budget.HibernateBudgetRepository;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.category.HibernateCategoryRepository;
import ru.naumen.personalfinancebot.repository.operation.HibernateOperationRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.HibernateUserRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.List;

/**
 * Фабрика, которая создает репозитории, работающие на Hibernate
 */
public class HibernateRepositoryFactory implements RepositoryFactory {

    /**
     * Менеджер, через который будут открываться транзакции
     */
    private final TransactionManager transactionManager;

    public HibernateRepositoryFactory(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public UserRepository newUserRepository() {
        return new HibernateUserRepository();
    }

    @Override
    public OperationRepository newOperationRepository() {
        return new HibernateOperationRepository();
    }

    @Override
    public CategoryRepository newCategoryRepository(List<Category> standardCategories) {
        return new HibernateCategoryRepository(transactionManager, standardCategories);
    }

    @Override
    public BudgetRepository newBudgetRepository() {
        return new HibernateBudgetRepository();
    }
}
