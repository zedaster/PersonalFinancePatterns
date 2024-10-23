package ru.naumen.personalfinancebot.repository.category;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.model.CategoryRow;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.model.category.CategoryComponent;
import ru.naumen.personalfinancebot.model.category.StandardCategory;
import ru.naumen.personalfinancebot.model.category.UserCategory;
import ru.naumen.personalfinancebot.repository.category.exception.ExistingStandardCategoryException;
import ru.naumen.personalfinancebot.repository.category.exception.ExistingUserCategoryException;
import ru.naumen.personalfinancebot.repository.category.exception.NotExistingCategoryException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для хранилища категорий
 */
public interface CategoryRepository {
    /**
     * Возвращает все категории указанного типа для указанного пользователя
     *
     * @param user Пользователь
     * @param type Тип категорий
     * @return Список из запрошенных категорий
     */
    List<CategoryComponent> getUserCategoriesByType(Session session, User user, CategoryType type);

    /**
     * Возвращает стандартную категорию по имени
     *
     * @param type         Тип категории
     * @param categoryName Имя категории
     * @return Стандартная категория
     */
    default Optional<CategoryComponent> getStandardCategoryByName(Session session, CategoryType type, String categoryName) {
        return getCategoryByName(session, null, type, categoryName);
    }

    /**
     * Возвращает все стандартные категории указанного типа
     *
     * @param type Тип категорий
     * @return Список из запрошенных категорий
     */
    List<CategoryComponent> getStandardCategoriesByType(Session session, CategoryType type);

    /**
     * Создаёт пользовательскую категорию
     *
     * @param categoryName Имя категории
     * @param type         Тип категории: расход / доход
     * @param user         Пользователь
     * @return Созданная категория
     * @throws ExistingUserCategoryException     если пользовательская категория с таким типом и именем для этого юзера уже существует
     * @throws ExistingStandardCategoryException если существует стандартная категория с таким же названием
     */
    UserCategory createUserCategory(Session session, User user, CategoryType type, String categoryName)
            throws ExistingUserCategoryException, ExistingStandardCategoryException;

    /**
     * Создаёт стандартную категорию, не относящуюся к пользователю
     *
     * @param categoryName Имя категории
     * @param type         Тип категории: расход / доход
     * @throws ExistingStandardCategoryException если стандартная категория с таким типом и именем уже существует
     * @return Созданная категория
     */
    StandardCategory createStandardCategory(Session session, CategoryType type, String categoryName)
            throws ExistingStandardCategoryException;

    /**
     * Удаляет пользовательскую категорию по названию
     *
     * @throws NotExistingCategoryException если такая категория не существует
     */
    void removeUserCategoryByName(Session session, User user, CategoryType type, String categoryName)
            throws NotExistingCategoryException;

    /**
     * Метод возвращает либо собственную категорию пользователя, либо стандартную.
     *
     * @param user         Пользователь
     * @param categoryName Название категории
     * @param type         Тип категории
     * @return Опциональный объект категории (пуст, если категория не найдена)
     */
    Optional<CategoryComponent> getCategoryByName(Session session, User user, CategoryType type, String categoryName);
}
