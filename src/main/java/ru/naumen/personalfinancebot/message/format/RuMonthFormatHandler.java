package ru.naumen.personalfinancebot.message.format;

import ru.naumen.personalfinancebot.service.OutputMonthFormatService;

import java.time.Month;

public class RuMonthFormatHandler implements FormatHandler {
    /**
     * Сервис, который форматирует месяц к русскому названию
     */
    private static final OutputMonthFormatService monthFormatService = new OutputMonthFormatService();

    private final Month value;

    public RuMonthFormatHandler(Month value) {
        this.value = value;
    }

    @Override
    public String handleString() {
        return monthFormatService.formatRuMonthName(value);
    }
}
