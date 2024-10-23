package ru.naumen.personalfinancebot.repository.operation;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.model.CategoryRow;
import ru.naumen.personalfinancebot.model.Operation;
import ru.naumen.personalfinancebot.model.User;

import java.time.LocalDate;

/**
 * Расширенный репозиторий модели данных "Операция" с использованием Hibernate, который позволяет дополнительно
 * добавить операции на определенную дату.
 */
public class FakeDatedOperationRepository extends HibernateOperationRepository {
    /**
     * Метод для добавления операции с определенной датой
     *
     * @param user      Пользователь, совершивший операцию
     * @param categoryRow  Категория дохода/расхода
     * @param payment   Сумма
     * @param createdAt Дата создания операции
     * @return совершённая операция
     */
    public Operation addOperation(Session session, User user, CategoryRow categoryRow, double payment, LocalDate createdAt) {
        Operation operation = new Operation(user, categoryRow, payment, createdAt);
        session.save(operation);
        return operation;
    }
}