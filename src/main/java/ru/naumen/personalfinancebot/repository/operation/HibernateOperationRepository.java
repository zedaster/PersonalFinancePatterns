package ru.naumen.personalfinancebot.repository.operation;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.model.CategoryRow;
import ru.naumen.personalfinancebot.model.CategoryType;
import ru.naumen.personalfinancebot.model.Operation;
import ru.naumen.personalfinancebot.model.User;
import ru.naumen.personalfinancebot.model.category.CategoryComponent;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Репозиторий модели данных "Операция" с использованием Hibernate
 */
public class HibernateOperationRepository implements OperationRepository {

    @Override
    public Operation addOperation(Session session, User user, CategoryComponent categoryComponent, double payment) {
        // TODO get category row from category repository
        Operation operation = new Operation(user, null, payment);
        session.save(operation);
        return operation;
    }

    @Override
    public Map<String, Double> getOperationsSumByType(Session session, User user, int month, int year, CategoryType type) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        final String hql = "SELECT cat.categoryName, sum (operation.payment) " +
                "FROM Operation operation " +
                "LEFT JOIN operation.categoryRow cat on cat.id = operation.categoryRow.id " +
                "WHERE cat.type = :categoryType " +
                "AND (operation.user = :user OR operation.user = NULL) " +
                "AND operation.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY operation.categoryRow.id, cat.id";

        List<?> operations = session.createQuery(hql)
                .setParameter("categoryType", type)
                .setParameter("user", user)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        if (operations.isEmpty()) {
            return null;
        }
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object operation : operations) {
            Object[] row = (Object[]) operation;
            String categoryRow = (String) row[0];
            Double payment = (Double) row[1];
            result.put(categoryRow, payment);
        }
        return result;
    }

    @Override
    public double getCurrentUserPaymentSummary(Session session, User user, CategoryType type, YearMonth yearMonth) {
        LocalDate startDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        String hql = "SELECT sum(op.payment) from Operation op "
                + "LEFT JOIN CategoryRow cat on cat.id = op.categoryRow.id "
                + "WHERE op.user = :user "
                + "AND cat.type = :type "
                + "AND op.createdAt BETWEEN :startDate AND :endDate";

        Object paymentSummary = session
                .createQuery(hql)
                .setParameter("user", user)
                .setParameter("type", type)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .uniqueResult();
        if (paymentSummary == null) {
            return 0.0;
        }
        return (double) paymentSummary;
    }

    @Override
    public Map<CategoryType, Double> getEstimateSummary(Session session, YearMonth yearMonth) {
        // HQL не поддерживает вложенный запрос в FROM

//            String hql = "SELECT paymentSums.type, avg(paymentSums.payments) FROM "
//                    + "(SELECT categories.type AS type, sum(operations.payment) AS payments "
//                    + "FROM Operation operations "
//                    + "JOIN operations.categoryRow categories "
//                    + "WHERE year(operations.createdAt) = :year "
//                    + "AND month(operations.createdAt) = :month "
//                    + "GROUP BY operations.user, categories.type) AS paymentSums "
//                    + "GROUP BY paymentSums.type";

        // Поэтому берем суммы и считаем среднее в Java

        String hql = "SELECT categories.type, sum(operations.payment) AS payments "
                + "FROM Operation operations "
                + "JOIN operations.categoryRow categories "
                + "WHERE year(operations.createdAt) = :year "
                + "AND month(operations.createdAt) = :month "
                + "GROUP BY operations.user, categories.type";

        List<?> sumRows = session.createQuery(hql)
                .setParameter("year", yearMonth.getYear())
                .setParameter("month", yearMonth.getMonth().getValue())
                .getResultList();

        if (sumRows.isEmpty()) {
            return null;
        }

        return calculateAverageForEachCategory(sumRows);
    }

    @Override
    public Map<String, Double> getAverageSummaryByStandardCategory(Session session, YearMonth yearMonth) {
        String averagePaymentHQL = """
                select categories.categoryName, sum(operations.payment) from Operation operations
                join operations.categoryRow categories
                where year(operations.createdAt) = :year
                and month(operations.createdAt) = :month
                and categories.user is null
                group by operations.user, operations.categoryRow.categoryName
                order by operations.categoryRow.categoryName asc
                """;
        List<?> result = session.createQuery(averagePaymentHQL)
                .setParameter("month", yearMonth.getMonth().getValue())
                .setParameter("year", yearMonth.getYear())
                .getResultList();

        if (result.isEmpty()) {
            return null;
        }

        return getAverageForCategoryPaymentList(result);
    }

    /**
     * Высчитывает среднюю величину значений для каждой категории
     *
     * @param sumRows Список строк из БД, полученый из hibernate. В нем должны быть CategoryType, затем сумма
     * @return Словарь<Тип категории, Среднее>
     */
    private Map<CategoryType, Double> calculateAverageForEachCategory(List<?> sumRows) {
        Map<CategoryType, Double> result = new HashMap<>();
        Map<CategoryType, Integer> count = new HashMap<>();

        for (Object rawRow : sumRows) {
            Object[] row = (Object[]) rawRow;
            CategoryType type = (CategoryType) row[0];
            double sum = (double) row[1];

            result.put(type, result.getOrDefault(type, 0.0) + sum);
            count.put(type, count.getOrDefault(type, 0) + 1);
        }

        result.replaceAll((type, value) -> result.get(type) / count.get(type));
        return result;
    }

    /**
     * Возвращает среднее значение из списка объектов
     *
     * @param objects Список объектов
     * @return Среднее значение
     */
    private Map<String, Double> getAverageForCategoryPaymentList(List<?> objects) {
        Map<String, Double> summaries = new LinkedHashMap<>();
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (Object rawRow : objects) {
            Object[] row = (Object[]) rawRow;
            String type = (String) row[0];
            double sum = (double) row[1];

            summaries.put(type, summaries.getOrDefault(type, 0.0) + sum);
            counts.put(type, counts.getOrDefault(type, 0) + 1);
        }

        summaries.replaceAll((type, value) -> summaries.get(type) / counts.get(type));
        return summaries;
    }
}
