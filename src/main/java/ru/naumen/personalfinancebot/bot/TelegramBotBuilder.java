package ru.naumen.personalfinancebot.bot;

import ru.naumen.personalfinancebot.configuration.TelegramBotConfiguration;
import ru.naumen.personalfinancebot.handler.FinanceBotHandler;
import ru.naumen.personalfinancebot.mode.FormatMode;
import ru.naumen.personalfinancebot.mode.NormalFormatMode;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

/**
 * Строитель для создания объекта {@link TelegramBot}
 */
// Паттерн Builder
public class TelegramBotBuilder {
    private TelegramBotConfiguration configuration;
    private FinanceBotHandler handler;
    private UserRepository userRepository;
    private TransactionManager transactionManager;
    private FormatMode formatMode = new NormalFormatMode();

    public TelegramBotBuilder setConfiguration(TelegramBotConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public TelegramBotBuilder setHandler(FinanceBotHandler handler) {
        this.handler = handler;
        return this;
    }

    public TelegramBotBuilder setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    public TelegramBotBuilder setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        return this;
    }

    public TelegramBotBuilder setMode(FormatMode formatMode) {
        this.formatMode = formatMode;
        return this;
    }

    public TelegramBot build() {
        return new TelegramBot(configuration, handler, userRepository, transactionManager, formatMode);
    }
}
