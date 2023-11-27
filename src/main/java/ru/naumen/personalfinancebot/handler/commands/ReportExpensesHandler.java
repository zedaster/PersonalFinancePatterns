package ru.naumen.personalfinancebot.handler.commands;

import ru.naumen.personalfinancebot.handler.event.HandleCommandEvent;
import ru.naumen.personalfinancebot.messages.StaticMessages;
import ru.naumen.personalfinancebot.repositories.operation.OperationRepository;
import ru.naumen.personalfinancebot.services.ReportService;

import java.util.List;
import java.util.Map;

/**
 * Обработчик для команды "/report_expense"
 *
 * @author Aleksandr Kornilov
 */
public class ReportExpensesHandler implements CommandHandler {
    private final ReportService reportService;

    public ReportExpensesHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Метод, вызываемый при получении команды "/report_expense"
     */
    @Override
    public void handleCommand(HandleCommandEvent event) {
        if (event.getArgs().size() != 1) {
            event.getBot().sendMessage(event.getUser(), StaticMessages.INCORRECT_SELF_REPORT_ARGS);
            return;
        }
        String report = this.reportService.getExpenseReport(event.getUser(), event.getArgs().get(0));
        event.getBot().sendMessage(event.getUser(), report);
    }
}
