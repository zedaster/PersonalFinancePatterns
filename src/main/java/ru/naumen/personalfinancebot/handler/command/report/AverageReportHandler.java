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

public class AverageReportHandler implements CommandHandler {
    /**
     * Сообщение о неверно переданном количестве аргументов
     */
    private final static String INCORRECT_ARGUMENT_COUNT = """
            Команда "/avg_report" не принимает аргументы, либо принимает Месяц и Год в формате "MM.YYYY"
            Например, "/avg_report" или "/avg_report 12.2023".""";

    /**
     * Сервис для парсинга даты
     */
    private final DateParseService dateParseService;

    /**
     * Сервис, который подготавливает отчёты
     */
    private final ReportService reportService;

    /**
     * @param dateParseService Сервис для парсинга даты
     * @param reportService    Сервис, который подготавливает отчёты
     */
    public AverageReportHandler(DateParseService dateParseService, ReportService reportService) {
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

        String report = this.reportService.getAverageReport(session, yearMonth);
        if (report == null) {
            commandData.getSender().sendMessage(
                    commandData.getUser(),
                    commandData.getArgs().isEmpty()
                            ? Message.CURRENT_DATA_NOT_EXISTS
                            : Message.DATA_NOT_EXISTS
            );
            return;
        }
        commandData.getSender().sendMessage(commandData.getUser(), report);
    }
}
