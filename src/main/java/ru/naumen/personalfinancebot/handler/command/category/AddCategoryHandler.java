package ru.naumen.personalfinancebot.handler.command.category;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.command.CommandHandler;
import ru.naumen.personalfinancebot.handler.command.HandleCommandException;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.handler.event.AddCategoryEvent;
import ru.naumen.personalfinancebot.handler.event.CommandEvent;
import ru.naumen.personalfinancebot.handler.event.CommandObservable;
import ru.naumen.personalfinancebot.handler.event.CommandObserver;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.category.exception.ExistingStandardCategoryException;
import ru.naumen.personalfinancebot.repository.category.exception.ExistingUserCategoryException;
import ru.naumen.personalfinancebot.service.CategoryParseService;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Обработчик команд для добавления пользовательской категории определенного типа
 *
 * @author Sergey Kazantsev
 */
public class AddCategoryHandler implements CommandHandler, CommandObservable {
    /**
     * Сообщение о существовании персональной (пользовательской) категории
     */
    private static final String USER_CATEGORY_ALREADY_EXISTS = "Персональная категория %s '%s' уже существует.";

    /**
     * Сообщение о существовании стандартной категории
     */
    private static final String STANDARD_CATEGORY_ALREADY_EXISTS = "Стандартная категория %s '%s' уже " +
                                                                   "существует.";

    /**
     * Сообщение об успешно созданной пользовательской категории
     */
    private static final String USER_CATEGORY_ADDED = "Категория %s '%s' успешно добавлена";

    /**
     * Тип категории, с которым будет работать обработчик
     */
    private final CategoryType type;

    /**
     * Хранилище категорий
     */
    private final CategoryRepository categoryRepository;

    /**
     * Сервис, который парсит категорию
     */
    private final CategoryParseService categoryParseService = CategoryParseService.getInstance();

    /**
     * Набор наблюдателей.
     * Используется LinkedHashSet для 1) уникальности элементов 2) быстрого перебора при вызове
     */
    private final Set<CommandObserver> observers = new LinkedHashSet<>();

    public AddCategoryHandler(CategoryType type, CategoryRepository categoryRepository) {
        this.type = type;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void handleCommand(CommandData commandData, Session session) throws HandleCommandException {
        String categoryName;
        try {
            categoryName = categoryParseService.parseCategory(commandData.getArgs());
        } catch (IllegalArgumentException ex) {
            throw new HandleCommandException(commandData, ex.getMessage());
        }

        String typeLabel = type.getPluralShowLabel();
        try {
            categoryRepository.createUserCategory(session, commandData.getUser(), type, categoryName);
        } catch (ExistingUserCategoryException e) {
            String responseText = USER_CATEGORY_ALREADY_EXISTS.formatted(typeLabel, categoryName);
            throw new HandleCommandException(commandData, responseText);
        } catch (ExistingStandardCategoryException e) {
            String responseText = STANDARD_CATEGORY_ALREADY_EXISTS.formatted(typeLabel, categoryName);
            throw new HandleCommandException(commandData, responseText);
        }

        String responseText = USER_CATEGORY_ADDED.formatted(typeLabel, categoryName);
        commandData.getSender().sendMessage(commandData.getUser(), responseText);

        CommandEvent event = new AddCategoryEvent(commandData, categoryName);
        observers.forEach(observer -> observer.handleEvent(event));
    }

    @Override
    public void addObserver(CommandObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(CommandObserver observer) {
        observers.remove(observer);
    }
}
