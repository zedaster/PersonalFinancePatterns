package ru.naumen.personalfinancebot.handler.command.report;

import org.hibernate.Session;
import ru.naumen.personalfinancebot.handler.command.CommandHandler;
import ru.naumen.personalfinancebot.handler.command.HandleCommandException;
import ru.naumen.personalfinancebot.handler.data.CommandData;
import ru.naumen.personalfinancebot.service.ReportService;

/**
 * Обработчик для команды "/report_expense"
 *
 * @author Aleksandr Kornilov
 */
public class ReportExpensesHandler implements CommandHandler {
    /**
     * Сообщение о неверно переданном количестве аргументов для команды /report_expense.
     */
    private static final String INCORRECT_SELF_REPORT_ARGS =
            "Команда /report_expense принимает 1 аргумент [mm.yyyy], например \"/report_expense 11.2023\"";

    /**
     * Сервис для составления отчета в строковом виде
     */
    private final ReportService reportService;

    public ReportExpensesHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void handleCommand(CommandData commandData, Session session) throws HandleCommandException {
        if (commandData.getArgs().size() != 1) {
            throw new HandleCommandException(commandData, INCORRECT_SELF_REPORT_ARGS);
        }
        String report = this.reportService.getExpenseReport(session, commandData.getUser(), commandData.getArgs().get(0));
        commandData.getSender().sendMessage(commandData.getUser(), report);
    }
}
