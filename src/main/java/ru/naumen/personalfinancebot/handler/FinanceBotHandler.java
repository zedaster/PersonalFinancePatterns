package ru.naumen.personalfinancebot.handler;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.command.CommandHandler;
import ru.naumen.personalfinancebot.handler.command.HandleCommandException;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.Optional;

/**
 * Обработчик операций для бота "Персональный финансовый трекер"
 */
public class FinanceBotHandler {
    /**
     * Сообщение, если пользователь передал неверную команду
     */
    private static final String COMMAND_NOT_FOUND = "Команда не распознана...";

    /**
     * История выполненных команд
     */
    private final CommandHistory commandHistory;

    private final CommandHolder commandHolder;

    /**
     * @param userRepository      Репозиторий для работы с пользователем
     * @param operationRepository Репозиторий для работы с операциями
     * @param categoryRepository  Репозиторий для работы с категориями
     * @param budgetRepository    Репозиторий для работы с бюджетами
     */
    public FinanceBotHandler(UserRepository userRepository,
                             OperationRepository operationRepository,
                             CategoryRepository categoryRepository,
                             BudgetRepository budgetRepository) {
        commandHistory = new CommandHistory();
        commandHolder = new CommandHolder(
                userRepository,
                operationRepository,
                categoryRepository,
                budgetRepository,
                commandHistory);
    }

    /**
     * Вызывается при получении какой-либо команды от пользователя
     */
    public void handleCommand(CommandData commandData, Session session) {
        Optional<CommandHandler> handler = this.commandHolder.getCommandHandler(commandData.getCommandName());
        if (handler.isEmpty()) {
            commandData.getSender().sendMessage(commandData.getUser(), COMMAND_NOT_FOUND);
            return;
        }

        try {
            handler.get().handleCommand(commandData, session);
            if (!this.commandHolder.isUndoCommand(commandData.getCommandName())) {
                commandHistory.add(commandData.toMemento());
            }
        } catch (HandleCommandException e) {
            commandData.getSender().sendMessage(commandData.getUser(), e.getMessage());
        }
    }
}
