package ru.naumen.personalfinancebot.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.naumen.personalfinancebot.configuration.TelegramBotConfiguration;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;

// Паттерн Adapter
public class TelegramBotAdapterImpl extends TelegramLongPollingBot implements TelegramAdapter {

    private static final String POLLING_EXCEPTION = "При запуске Телеграм Бота произошла ошибка. ";

    /**
     * Конфигурация телеграм бота
     */
    private final TelegramBotConfiguration configuration;

    /**
     * Обработчик обновлений в телеграм боте
     */
    private final UpdateHandler updateHandler;

    /**
     * Менеджер транзакций
     */
    private final TransactionManager transactionManager;

    /**
     * Репозиторий для работы с пользователем
     */
    private final UserRepository userRepository;

    public TelegramBotAdapterImpl(TelegramBotConfiguration configuration, UpdateHandler updateHandler, TransactionManager transactionManager, UserRepository userRepository) {
        super(configuration.getBotToken());
        this.configuration = configuration;
        this.updateHandler = updateHandler;
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
            try {
                transactionManager.produceTransaction(session -> {
                    Long chatId = update.getMessage().getChatId();
                    Optional<User> user = this.userRepository.getUserByTelegramChatId(session, chatId);
                    if (user.isEmpty()) {
                        user = Optional.of(new User(chatId, 0));
                        this.userRepository.saveUser(session, user.get());
                    }

                    List<String> msgWords = List.of(update.getMessage().getText().split(" "));
                    String cmdName = msgWords.get(0).substring(1);
                    List<String> args = msgWords.subList(1, msgWords.size());

                    updateHandler.onCommandReceived(session, user.get(), cmdName, args);
                });
            } catch (RuntimeException e) {
                System.err.println("Произошла ошибка во время обработки команды в боте:");
                e.printStackTrace();
            }

        }
    }

    /**
     * Возвращает bot username
     * Метод необходим для библиотеки telegrambots
     */
    @Override
    public String getBotUsername() {
        return this.configuration.getBotName();
    }

    /**
     * Запуск бота
     */
    public void startPooling() throws PoolingException {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("Telegram bot is pooling now...");
        } catch (TelegramApiException exception) {
            throw new PoolingException(POLLING_EXCEPTION, exception);
        }
    }

    /**
     * Отправка текстового сообщения определенному пользователю
     */
    public void sendMessage(User user, String text) {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(user.getChatId());
        message.setText(text);

        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
