package me.zedaster.financeadminui.frame;

import me.zedaster.financeadminui.component.*;
import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.CommandSender;
import ru.naumen.personalfinancebot.handler.command.AddCategoryHandler;
import ru.naumen.personalfinancebot.handler.command.CommandHandler;
import ru.naumen.personalfinancebot.handler.commandData.CommandData;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.Arrays;
import java.util.List;

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

    private final CategoryRepository categoryRepository;

    public AddCategoryFrameManager(TransactionManager transactionManager, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
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
    }

    @Override
    public void notify(Component sender, FrameEvent event) {
        if (sender == this.addButton && event == FrameEvent.BUTTON_PUSHED) {
            addCategory();
            return;
        }
        if (sender == this.cancelButton && event == FrameEvent.BUTTON_PUSHED) {
            setVisible(false);
            return;
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

            CommandHandler command = new AddCategoryHandler(typeSelector.getValue(), categoryRepository);
            command.handleCommand(new CommandData(
                    this,
                    user,
                    null,
                    Arrays.asList(this.categoryNameField.getValue().split(" "))),
                    session);
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
