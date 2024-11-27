package ru.naumen.personalfinancebot.bot;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.configuration.TelegramBotConfiguration;
import ru.naumen.personalfinancebot.handler.FinanceBotHandler;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.mode.FormatMode;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.List;

/**
 * Телеграм бот
 */

public class TelegramBot extends Bot implements UpdateHandler {

    /**
     * Класс-обработчик команд
     */
    private final FinanceBotHandler botHandler;

    /**
     * Режим работы бота
     */
    private final FormatMode formatMode;

    /**
     * Адаптер на библиотеку
     */
    private final TelegramAdapter adapter;

    /**
     * @param configuration      Настройки для телеграм бота
     * @param botHandler         Класс-обработчик команд
     * @param userRepository     Репозиторий для работы с пользователем
     * @param transactionManager Менеджер транзакций
     */
    public TelegramBot(
            TelegramBotConfiguration configuration,
            FinanceBotHandler botHandler,
            UserRepository userRepository,
            TransactionManager transactionManager,
            FormatMode formatMode) {
        super(formatMode);
        this.adapter = new TelegramBotAdapterImpl(configuration, this, transactionManager, userRepository);
        this.botHandler = botHandler;
        this.formatMode = formatMode;
    }

    /**
     * Обработчик новых событий из библиотеки telegrambots
     */
    @Override
    public void onCommandReceived(Session session, User user, String cmdName, List<String> args) {
        CommandData commandData = formatMode.formatCommandData(this, user, cmdName, args);
        this.botHandler.handleCommand(commandData, session);
    }

    /**
     * Запуск бота
     */
    @Override
    public void startPooling() throws PoolingException {
        adapter.startPooling();
    }

    /**
     * Отправка текстового сообщения определенному пользователю
     */
    @Override
    protected void internalSendMessage(User user, String text) {
        adapter.sendMessage(user, text);
    }
}
