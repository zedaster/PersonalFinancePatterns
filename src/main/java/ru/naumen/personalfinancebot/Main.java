package ru.naumen.personalfinancebot;

import org.hibernate.SessionFactory;
import ru.naumen.personalfinancebot.bot.Bot;
import ru.naumen.personalfinancebot.bot.PoolingException;
import ru.naumen.personalfinancebot.bot.TelegramBotBuilder;
import ru.naumen.personalfinancebot.configuration.HibernateConfiguration;
import ru.naumen.personalfinancebot.configuration.StandardCategoryConfiguration;
import ru.naumen.personalfinancebot.configuration.TelegramBotConfiguration;
import ru.naumen.personalfinancebot.handler.FinanceBotHandler;
import ru.naumen.personalfinancebot.mode.NormalFormatMode;
import ru.naumen.personalfinancebot.model.Category;
import ru.naumen.personalfinancebot.repository.HibernateRepositoryFactory;
import ru.naumen.personalfinancebot.repository.RepositoryFactory;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.budget.BudgetRepository;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.operation.OperationRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.List;

/**
 * Программа, запускающая Телеграм-бота
 */
public class Main {
    public static void main(String[] args) {
        HibernateConfiguration hibernateConfiguration = new HibernateConfiguration(
                System.getenv("DB_URL"),
                System.getenv("DB_USERNAME"),
                System.getenv("DB_PASSWORD"));
        SessionFactory sessionFactory = hibernateConfiguration.getSessionFactory();
        TransactionManager transactionManager = new TransactionManager(sessionFactory);

        List<Category> standardCategories = StandardCategoryConfiguration
                .getInstance()
                .getStandardCategories();

        RepositoryFactory repositoryFactory = new HibernateRepositoryFactory(transactionManager);
        UserRepository userRepository = repositoryFactory.newUserRepository();
        OperationRepository operationRepository = repositoryFactory.newOperationRepository();
        CategoryRepository categoryRepository = repositoryFactory.newCategoryRepository(standardCategories);
        BudgetRepository budgetRepository = repositoryFactory.newBudgetRepository();

        FinanceBotHandler handler = new FinanceBotHandler(
                userRepository,
                operationRepository,
                categoryRepository,
                budgetRepository
        );

        Bot bot = new TelegramBotBuilder()
                .setConfiguration(TelegramBotConfiguration.fromEnv())
                .setHandler(handler)
                .setUserRepository(userRepository)
                .setTransactionManager(transactionManager)
                .setMode(new NormalFormatMode())
                .build();

        try {
            bot.startPooling();
        } catch (PoolingException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }
}
