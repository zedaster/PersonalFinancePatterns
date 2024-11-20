package me.zedaster.financeadminui;

import me.zedaster.financeadminui.frame.AddCategoryFrameManager;
import org.hibernate.SessionFactory;
import ru.naumen.personalfinancebot.configuration.HibernateConfiguration;
import ru.naumen.personalfinancebot.configuration.StandardCategoryConfiguration;
import ru.naumen.personalfinancebot.model.Category;
import ru.naumen.personalfinancebot.repository.HibernateRepositoryFactory;
import ru.naumen.personalfinancebot.repository.RepositoryFactory;
import ru.naumen.personalfinancebot.repository.TransactionManager;
import ru.naumen.personalfinancebot.repository.category.CategoryRepository;
import ru.naumen.personalfinancebot.repository.user.UserRepository;

import java.util.List;

// Регистрация нового пользователя
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
        CategoryRepository categoryRepository = repositoryFactory.newCategoryRepository(standardCategories);

        AddCategoryFrameManager addBudgetFrame = new AddCategoryFrameManager(transactionManager, userRepository, categoryRepository);
        addBudgetFrame.setVisible(true);
    }
}
