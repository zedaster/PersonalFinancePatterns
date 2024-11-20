package ru.naumen.personalfinancebot.model;

/**
 * Enum для обозначения типа категории:
 * INCOME - ДОХОД, EXPENSE - РАСХОД;
 * Важно не менять порядок внутри этого enum, т.к. от него зависит поле {@link Category#getType()}
 */
public enum CategoryType {
    INCOME("income", "доходов"),
    EXPENSE("expenses", "расходов");

    /**
     * Строковое именование категории
     */
    private final String label;

    /**
     * Название, которое будет показано при выводе множества категории этого типа
     */
    private final String pluralShowLabel;

    CategoryType(String label, String pluralShowLabel) {
        this.label = label;
        this.pluralShowLabel = pluralShowLabel;
    }

    /**
     * Выводит именование категории, используемое для их вывода куда-либо со словом "категории ".
     * Для CategoryType.INCOME будет выведено "доходов",
     * а для CategoryType.EXPENSE - "расходов".
     */
    public String getPluralShowLabel() {
        return pluralShowLabel;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
