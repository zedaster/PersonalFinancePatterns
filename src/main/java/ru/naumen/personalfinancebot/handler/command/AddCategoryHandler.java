package ru.naumen.personalfinancebot.handler.command;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.commandData.CommandData;
import ru.naumen.personalfinancebot.message.Message;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.category.exceptions.CreatingExistingStandardCategoryException;
import ru.naumen.personalfinancebot.repository.category.exceptions.CreatingExistingUserCategoryException;
import ru.naumen.personalfinancebot.service.ArgumentParseService;

/**
 * Обработчик команд для добавления пользовательской категории определенного типа
 *
 * @author Sergey Kazantsev
 */
public class AddCategoryHandler implements CommandHandler {
    /**
     * Тип категории, с которым будет работать обработчик
     */
    private final CategoryType type;

    /**
     * Хранилище категорий
     */
    private final CategoryRepository categoryRepository;

    /**
     * Сервис, который парсит аргументы
     */
    private final ArgumentParseService argumentParser;

    public AddCategoryHandler(CategoryType type, CategoryRepository categoryRepository, ArgumentParseService argumentParser) {
        this.type = type;
        this.categoryRepository = categoryRepository;
        this.argumentParser = argumentParser;
    }

    /**
     * Метод, вызываемый при получении команды
     */
    @Override
    public void handleCommand(CommandData commandData, Session session) {
        String categoryName;
        try {
            categoryName = argumentParser.parseCategory(commandData.getArgs());
        } catch (IllegalArgumentException ex) {
            commandData.getBot().sendMessage(commandData.getUser(), ex.getMessage());
            return;
        }

        String typeLabel = type.getPluralShowLabel();
        try {
            categoryRepository.createUserCategory(session, commandData.getUser(), type, categoryName);
        } catch (CreatingExistingUserCategoryException e) {
            String responseText = Message.USER_CATEGORY_ALREADY_EXISTS
                    .replace("{type}", typeLabel)
                    .replace("{name}", categoryName);
            commandData.getBot().sendMessage(commandData.getUser(), responseText);
            return;
        } catch (CreatingExistingStandardCategoryException e) {
            String responseText = Message.STANDARD_CATEGORY_ALREADY_EXISTS
                    .replace("{type}", typeLabel)
                    .replace("{name}", categoryName);
            commandData.getBot().sendMessage(commandData.getUser(), responseText);
            return;
        }

        String responseText = Message.USER_CATEGORY_ADDED
                .replace("{type}", typeLabel)
                .replace("{name}", categoryName);
        commandData.getBot().sendMessage(commandData.getUser(), responseText);
    }
}
