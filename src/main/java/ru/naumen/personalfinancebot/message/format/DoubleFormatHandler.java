package ru.naumen.personalfinancebot.message.format;

import ru.naumen.personalfinancebot.service.OutputNumberFormatService;

public class DoubleFormatHandler implements FormatHandler {

    /**
     * Сервис, который форматирует числа
     */
    private static final OutputNumberFormatService numberFormatService = new OutputNumberFormatService();

    private final double value;

    public DoubleFormatHandler(double value) {
        this.value = value;
    }

    @Override
    public String handleString() {
        return numberFormatService.formatDouble(value);
    }
}
