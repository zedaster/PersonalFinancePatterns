package ru.naumen.personalfinancebot.handler.command.report;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.command.CommandHandler;
import ru.naumen.personalfinancebot.handler.command.HandleCommandException;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.message.Message;
import ru.naumen.personalfinancebot.service.DateParseService;
import ru.naumen.personalfinancebot.service.ReportService;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * Класс для обработки команды "/estimate_report"
 */
public class EstimateReportHandler implements CommandHandler {
    /**
     * Сервис для парсинга даты из аргументов
     */
    private final DateParseService dateParseService;

    /**
     * Сервис для подготовки отчетов
     */
    private final ReportService reportService;

    /**
     * Сообщение, выводимое при недопустимом количестве аргументов
     */
    private static final String INCORRECT_ARGUMENT_COUNT = """
            Команда "/estimate_report" не принимает аргументов, либо принимает Месяц и Год в формате "MM.YYYY".
            Например, "/estimate_report" или "/estimate_report 12.2023".""";

    public EstimateReportHandler(DateParseService dateParseService, ReportService reportService) {
        this.dateParseService = dateParseService;
        this.reportService = reportService;
    }

    @Override
    public void handleCommand(CommandData commandData, Session session) throws HandleCommandException {
        YearMonth yearMonth;
        try {
            yearMonth = this.dateParseService.parseYearMonthArgs(commandData.getArgs());
        } catch (DateTimeParseException exception) {
            throw new HandleCommandException(commandData, Message.INCORRECT_YEAR_MONTH_FORMAT);
        } catch (IllegalArgumentException exception) {
            throw new HandleCommandException(commandData, INCORRECT_ARGUMENT_COUNT);
        }

        String report = this.reportService.getEstimateReport(session, yearMonth);
        if (report == null) {
            if (commandData.getArgs().isEmpty()) {
                throw new HandleCommandException(commandData, Message.CURRENT_DATA_NOT_EXISTS);
            }
            throw new HandleCommandException(commandData, Message.DATA_NOT_EXISTS);
        }
        commandData.getSender().sendMessage(commandData.getUser(), report);
    }
}
