package ru.naumen.personalfinancebot.message.format;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

// Паттерн Chain of Responsibility
/**
 * Строитель сообщений на вывод
 */
public class MessageBuilder {
    /**
     * Обработчики меток
     */
    private final List<FormatHandler> handlers;

    /**
     * Изначальный текст
     */
    private final String text;

    /**
     * Создает форматировщик для вывода сообщения
     * @param text Текст (в котором метки %s будут заменяться на обработанные строки)
     */
    public MessageBuilder(String text) {
        this.text = text;
        this.handlers = new ArrayList<>();
    }

    private MessageBuilder(String text, List<FormatHandler> handlers) {
        this.text = text;
        this.handlers = handlers;
    }

    /**
     * Заменяет следующую метку на месяц
     * @param value Месяц
     * @return Новый объект MessageBuilder
     */
    public MessageBuilder nextRuMonth(Month value) {
        return this.newWithAddedHandler(new RuMonthFormatHandler(value));
    }

    /**
     * Заменяет следующую метку на целочисленное значение
     * @param value целочисленное значение
     * @return Новый объект MessageBuilder
     */
    public MessageBuilder nextInteger(int value) {
        return this.newWithAddedHandler(new IntegerFormatHandler(value));
    }

    /**
     * Заменяет следующую метку на число с плавающей точкой
     * @param value число с плавающей точкой
     * @return Новый объект MessageBuilder
     */
    public MessageBuilder nextDouble(double value) {
        return this.newWithAddedHandler(new DoubleFormatHandler(value));
    }

    /**
     * Заменяет следующую метку на значение из обработчика
     * @param formatHandler обработчик
     */
    public MessageBuilder next(FormatHandler formatHandler) {
        return this.newWithAddedHandler(formatHandler);
    }

    /**
     * Строит строку
     * @return Строку с замененными метками
     */
    public String buildString() {
        List<String> values = new ArrayList<>();
        for (FormatHandler handler : handlers) {
            values.add(handler.handleString());
        }
        return text.formatted(values);
    }

    private MessageBuilder newWithAddedHandler(FormatHandler newHandler) {
        List<FormatHandler> newHandlers = new ArrayList<>(this.handlers);
        newHandlers.add(newHandler);
        return new MessageBuilder(this.text, newHandlers);
    }
}
