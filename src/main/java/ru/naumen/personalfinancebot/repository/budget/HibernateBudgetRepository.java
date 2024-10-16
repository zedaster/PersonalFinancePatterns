package ru.naumen.personalfinancebot.repository.budget;

import com.sun.istack.Nullable;
import org.hibernate.Session;
import ru.naumen.personalfinancebot.model.Budget;
import ru.naumen.personalfinancebot.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link BudgetRepository}
 * Репозиторий для работы с моделью данных {@link Budget}
 */
public class HibernateBudgetRepository implements BudgetRepository {
    @Override
    public void saveBudget(Session session, Budget budget) {
        session.save(budget);
    }

    @Override
    public Optional<Budget> getBudget(Session session, User user, @Nullable YearMonth yearMonth) {
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Budget> criteriaQuery = criteriaBuilder.createQuery(Budget.class);
        Root<Budget> root = criteriaQuery.from(Budget.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("user"), user))
                .where(
                        criteriaBuilder.equal(
                                criteriaBuilder.function("YEAR", Integer.class, root.get("targetDate")),
                                yearMonth.getYear()
                        ))
                .where(
                        criteriaBuilder.equal(
                                criteriaBuilder.function("MONTH", Integer.class, root.get("targetDate")),
                                yearMonth.getMonth().getValue()
                        ));
        return session.createQuery(criteriaQuery).uniqueResultOptional();
    }

    @Override
    public List<Budget> selectBudgetRange(Session session, User user, YearMonth from, YearMonth to) {
        LocalDate startDate = LocalDate.of(from.getYear(), from.getMonth(), 1);
        LocalDate endDate = LocalDate.of(to.getYear(), to.getMonth(), 1)
                .plusMonths(1)
                .minusDays(1);

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Budget> criteriaQuery = criteriaBuilder.createQuery(Budget.class);
        Root<Budget> root = criteriaQuery.from(Budget.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("user"), user))
                .where(
                        criteriaBuilder.between(root.get("targetDate"), startDate, endDate)
                )
                .orderBy(criteriaBuilder.asc(root.get("targetDate")));
        return session.createQuery(criteriaQuery).getResultList();
    }
}
