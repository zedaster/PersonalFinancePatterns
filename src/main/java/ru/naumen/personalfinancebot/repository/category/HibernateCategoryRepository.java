package ru.naumen.personalfinancebot.repository.category;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.naumen.personalfinancebot.model.CategoryRow;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.model.category.*;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.category.exception.ExistingStandardCategoryException;
import ru.naumen.personalfinancebot.repository.category.exception.ExistingUserCategoryException;
import ru.naumen.personalfinancebot.repository.category.exception.NotExistingCategoryException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Хранилище категорий с использованием Hibernate
 */
public class HibernateCategoryRepository implements CategoryRepository {

    /**
     * Создает репозиторий для категорий.
     */
    public HibernateCategoryRepository() {
        // empty
    }

    /**
     * Создает репозиторий для категорий и добавляет стандартные категории, если их не существует.
     *
     * @param transactionManager Нужен для открытия транзакции на добавление категорий
     * @param categories         Список стандартных категорий.
     */
    public HibernateCategoryRepository(TransactionManager transactionManager, List<CategoryComponent> categories) {
        transactionManager.produceTransaction(session -> {
            for (CategoryComponent category : categories) {
                this.saveCategoryComponentIfNotExists(session, category);
            }
        });
    }

    private void saveCategoryComponentIfNotExists(Session session, CategoryComponent category) {
        saveCategoryComponentIfNotExists(session, category, "");
    }

    private void saveCategoryComponentIfNotExists(Session session, CategoryComponent category, String lastPath) {
        String currentPath;
        if (lastPath.isEmpty()) {
            currentPath = category.getName();
        } else {
            currentPath = lastPath + "/" + category.getName();
        }

        if (category instanceof StandardCategory standardCategory) {
            CategoryRow categoryRow = new CategoryRow(null, currentPath, standardCategory.getType());
            Optional<CategoryComponent> existingCategory = this.getStandardCategoryByName(session, categoryRow.getType(),
                    categoryRow.getCategoryName());
            if (existingCategory.isEmpty()) {
                session.save(categoryRow);
            }
        } else if (category instanceof CategoryGroup) {
            category.children().forEach(child -> saveCategoryComponentIfNotExists(session, child, currentPath));
        }
    }

    @Override
    public List<CategoryComponent> getUserCategoriesByType(Session session, @NotNull User user, CategoryType type) {
        return getCategoriesByType(session, user, type);
    }

    @Override
    public List<CategoryComponent> getStandardCategoriesByType(Session session, CategoryType type) {
        return getCategoriesByType(session, null, type);
    }

    @Override
    public UserCategory createUserCategory(Session session, User user, CategoryType type, String categoryName) throws
            ExistingStandardCategoryException, ExistingUserCategoryException {
        Optional<CategoryComponent> existingUserCategory = this.getCategoryByName(session, user, type, categoryName);

        if (existingUserCategory.isPresent()) {
            if (existingUserCategory.get() instanceof StandardCategory) {
                throw new ExistingStandardCategoryException(categoryName);
            } else {
                throw new ExistingUserCategoryException(categoryName);
            }
        }

        CategoryRow categoryRow = new CategoryRow();
        categoryRow.setCategoryName(categoryName);
        categoryRow.setType(type);
        categoryRow.setUser(user);
        createCategory(session, categoryRow);

        return new UserCategory(categoryName, type, user);
    }

    @Override
    public StandardCategory createStandardCategory(Session session, CategoryType type, String categoryName)
            throws ExistingStandardCategoryException {
        if (this.getStandardCategoryByName(session, type, categoryName).isPresent()) {
            throw new ExistingStandardCategoryException(categoryName);
        }

        CategoryRow categoryRow = new CategoryRow();
        categoryRow.setCategoryName(categoryName);
        categoryRow.setType(type);
        createCategory(session, categoryRow);

        return new StandardCategory(categoryName, type);
    }

    public void removeUserCategoryByName(Session session, User user, CategoryType type, String categoryName)
            throws NotExistingCategoryException {
        Optional<CategoryComponent> category = getCategoryByName(session, user, type, categoryName);
        if (category.isEmpty() || category.get() instanceof StandardCategory) {
            throw new NotExistingCategoryException(categoryName);
        }
        session.delete(category.get());
    }

    @Override
    public Optional<CategoryComponent> getCategoryByName(Session session, @Nullable User user, CategoryType type, String categoryName) {
        Query<CategoryRow> resultQuery;

        if (user == null) {
            resultQuery = selectCategoriesSeparately(session, type, null, categoryName);
        } else {
            resultQuery = selectCategoriesTogether(session, type, user, categoryName);
        }

        Optional<CategoryRow> categoryRow = resultQuery
                .getResultStream()
                .findFirst();

        if (categoryRow.isEmpty()) {
            return Optional.empty();
        }

        if (user == null) {
            return Optional.of(new StandardCategory(categoryRow.get().getCategoryName(), categoryRow.get().getType()));
        }

        return Optional.of(new UserCategory(categoryRow.get().getCategoryName(), categoryRow.get().getType(), user));
    }

    /**
     * Получает либо пользовательские, либо стандартные (при user = null) категории определенного типа.
     * Регистр названия категории игнорируется.
     */
    private List<CategoryComponent> getCategoriesByType(Session session, @Nullable User user, CategoryType type) {
        Query<CategoryRow> query = selectCategoriesSeparately(session, type, user, null);
        if (user == null) {
            return query.getResultStream()
                    .map(row -> (CategoryComponent) new StandardCategory(row.getCategoryName(), row.getType()))
                    .toList();
        }

        return query.getResultStream()
                .map(row -> (CategoryComponent) new UserCategory(row.getCategoryName(), row.getType(), user))
                .toList();
    }

    /**
     * Делает запрос категорий в БД. Возвращает запрос, содержащий <b>или</b> стандартные категории при user == null,
     * <b>или</b> персональные категории в ином случае.
     * Регистр названия категории при выборке игнорируется.
     */
    private Query<CategoryRow> selectCategoriesSeparately(Session session, CategoryType type, @Nullable User user,
                                                          @Nullable String categoryName) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CategoryRow> cq = cb.createQuery(CategoryRow.class);
        Root<CategoryRow> root = cq.from(CategoryRow.class);

        List<Predicate> selectPredicates = new ArrayList<>();

        Predicate userIdEquity;
        if (user != null) {
            userIdEquity = cb.equal(root.get("user"), user.getId());
        } else {
            userIdEquity = cb.isNull(root.get("user"));
        }
        selectPredicates.add(userIdEquity);

        Predicate categoryTypeEquity = cb.equal(root.get("type"), type);
        selectPredicates.add(categoryTypeEquity);

        if (categoryName != null) {
            Predicate categoryNameEquity = cb.equal(cb.lower(root.get("categoryName")), categoryName.toLowerCase());
            selectPredicates.add(categoryNameEquity);
        }

        Predicate[] selectPredicatesArray = selectPredicates.toArray(new Predicate[0]);
        cq.select(root).where(cb.and(selectPredicatesArray));
        return session.createQuery(cq);
    }

    /**
     * Делает запрос категорий в БД. Возвращает запрос, содержащий <b>и</b> стандартные категории,
     * <b>и</b> персональные категории.
     * Регистр названия категории при выборке игнорируется.
     */
    private Query<CategoryRow> selectCategoriesTogether(Session session, CategoryType type, User user, String categoryName) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CategoryRow> cq = cb.createQuery(CategoryRow.class);
        Root<CategoryRow> root = cq.from(CategoryRow.class);

        cq.select(root).where(cb.and(
                cb.or(
                        cb.equal(root.get("user"), user.getId()), // userId == userId
                        cb.isNull(root.get("user")) // userId is null
                ),
                cb.equal(root.get("type"), type),
                cb.equal(cb.lower(root.get("categoryName")), categoryName.toLowerCase())
        ));

        return session.createQuery(cq);
    }

    /**
     * Делегирующий метод для создания записи категории в базе данных
     *
     * @param categoryRow Категория
     * @return Категория
     */
    private CategoryRow createCategory(Session session, CategoryRow categoryRow) {
        session.save(categoryRow);
        return categoryRow;
    }
}
