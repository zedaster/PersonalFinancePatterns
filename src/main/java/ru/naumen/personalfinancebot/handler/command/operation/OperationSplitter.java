package ru.naumen.personalfinancebot.handler.command.operation;

import ru.naumen.personalfinancebot.handler.command.ArgumentSplitter;
import ru.naumen.personalfinancebot.handler.command.ArgumentSplitterException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// Паттерн Iterator
public class OperationSplitter extends ArgumentSplitter {
    /**
     * Сообщение о неверно переданном количестве аргументов
     */
    private static final String INCORRECT_ARGUMENTS =
            "Данная команда принимает аргументы: [payment - сумма 1] [категория расхода/дохода 1] [payment - сумма 2] [категория расхода/дохода 2] ...";

    /**
     * Сообщение о неверно переданном аргументе, который отвечает за сумму операции
     */
    private static final String INCORRECT_PAYMENT_ARG = "Одна из сумм операций указана в неверном формате. Передайте корректное положительно число";


    private int i = 0;

    public OperationSplitter(List<String> arguments) {
        super(arguments);
        if (arguments.size() < 2) {
            throw new ArgumentSplitterException(INCORRECT_ARGUMENTS);
        }
    }

    @Override
    public boolean hasNext() {
        return i >= arguments.size();
    }

    @Override
    public List<String> next() {
        if (i >= arguments.size()) {
            throw new NoSuchElementException();
        }

        List<String> subArgs = new ArrayList<>();

        String currentArg = arguments.get(i);
        if (!isDouble(currentArg)) {
            throw new ArgumentSplitterException(INCORRECT_PAYMENT_ARG);
        }
        subArgs.add(currentArg);

        if (i + 1 == arguments.size()) {
            throw new ArgumentSplitterException(INCORRECT_ARGUMENTS);
        }

        String nextArg = arguments.get(i + 1);
        if (isDouble(nextArg)) {
            throw new ArgumentSplitterException(INCORRECT_ARGUMENTS);
        }

        do {
            currentArg = nextArg;
            nextArg = arguments.get(i + 1);
            subArgs.add(currentArg);
            i++;
        } while (hasNext() && !isDouble(nextArg));

        return subArgs;
    }

    private boolean isDouble(String arg) {
        try {
            double sum = Double.parseDouble(arg);
            if (sum <= 0) {
                throw new ArgumentSplitterException(INCORRECT_PAYMENT_ARG);
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
