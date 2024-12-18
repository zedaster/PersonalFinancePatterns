package me.zedaster.financeadminui.frame;

import me.zedaster.financeadminui.component.*;
import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.CommandSender;
import ru.naumen.personalfinancebot.handler.command.HandleCommandException;
import ru.naumen.personalfinancebot.handler.command.category.AddCategoryHandler;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Паттерн Mediator
public class AddCategoryFrameManager implements FrameManager, CommandSender {
    private final FormFrame formFrame;

    private final Field userIdField;

    private final Selector<CategoryType> typeSelector;

    private final Field categoryNameField;

    private final ActionButton addButton;

    private final ActionButton cancelButton;

    private final NotificationView notificationView;

    private final TransactionManager transactionManager;

    private final UserRepository userRepository;

    private final Map<CategoryType, AddCategoryHandler> addCommandHandlers;

    public AddCategoryFrameManager(TransactionManager transactionManager, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
        this.userIdField = new Field(this, "User Id");
        this.typeSelector = new Selector<>(this, "Тип категории", List.of(CategoryType.values()));
        this.categoryNameField = new Field(this, "Имя категории");
        this.addButton = new ActionButton(this, "Добавить");
        this.cancelButton = new ActionButton(this, "Отменить");
        this.formFrame = new FormFrame(
                this,
                "Добавить категорию",
                this.userIdField,
                this.categoryNameField,
                new ActionLayout(
                        this.addButton,
                        this.cancelButton
                )
        );
        this.notificationView = new NotificationView();

        this.addCommandHandlers = new HashMap<>();
        AddCategoryNotificator addCategoryNotificator = new AddCategoryNotificator();
        for (CategoryType type : CategoryType.values()) {
            AddCategoryHandler handler = new AddCategoryHandler(type, categoryRepository);
            handler.addObserver(addCategoryNotificator);
            this.addCommandHandlers.put(type, handler);
        }
    }

    @Override
    public void notify(Component sender, FrameEvent event) {
        if (sender == this.addButton && event == FrameEvent.BUTTON_PUSHED) {
            addCategory();
            return;
        }
        if (sender == this.cancelButton && event == FrameEvent.BUTTON_PUSHED) {
            setVisible(false);
        }
    }

    private void addCategory() {
        transactionManager.produceTransaction(session -> {
            long userId;
            User user;
            try {
                userId = parsePositiveLong(userIdField.getValue());
            } catch (NumberFormatException e) {
                this.notificationView.showNotification("ID пользователя введен неверно!");
                return;
            }
            try {
                user = findUserById(session, userId);
            } catch (IllegalArgumentException e) {
                this.notificationView.showNotification("Пользователь с таким ID не найден!");
                return;
            }

            AddCategoryHandler commandHandler = addCommandHandlers.get(typeSelector.getValue());
            try {
                commandHandler.handleCommand(new CommandData(
                        this,
                        user,
                        null,
                        Arrays.asList(this.categoryNameField.getValue().split(" "))),
                        session);
            } catch (HandleCommandException e) {
                this.notificationView.showNotification(e.getMessage());
            }
        });
    }

    private long parsePositiveLong(String value) {
        long number = Long.parseLong(value);
        if (number < 0) {
            throw new NumberFormatException("The number is less than zero!");
        }
        return number;
    }

    private User findUserById(Session session, Long userId) {
        return this.userRepository.getUserById(session, userId).orElseThrow(() ->
                new IllegalArgumentException("User not found"));
    }

    @Override
    public void sendMessage(User user, String message) {
        this.notificationView.showNotification(message);
    }

    public void setVisible(boolean visibility) {
        this.formFrame.setVisible(visibility);
    }
}
