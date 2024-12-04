package example.patterns.state.service;

import example.patterns.state.User;

import java.time.YearMonth;

public class BudgetService {
    private static final BudgetService instance = new BudgetService();

    private BudgetService() {

    }

    public static BudgetService getInstance() {
        return instance;
    }

    public void createBudget(User user, YearMonth yearMonth, double income, double expenses) {
        // Здесь логика для сохранения бюджета
    }
}
