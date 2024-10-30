package ru.naumen.personalfinancebot.handler.validator;

import ru.naumen.personalfinancebot.service.DateParseService;
import ru.naumen.personalfinancebot.service.NumberParseService;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Iterator;

// Паттерн Фасад
public class ArgumentValidator {

    /**
     * Сервис, который парсит дату
     */
    private final DateParseService dateParseService = new DateParseService();

    /**
     * Сервис, который парсит числа
     */
    private final NumberParseService numberParseService = new NumberParseService();

    private final Iterator<String> argIterator;

    private final int size;

    public ArgumentValidator(Collection<String> args) {
        this.argIterator = args.iterator();
        this.size = args.size();
    }

    public void validateLength(int expectLength, String invalidMessage) throws ArgumentValidatorException {
        if (size != expectLength) {
            throw new ArgumentValidatorException("Count of arguments is incorrect!", invalidMessage);
        }
    }

    public YearMonth parseNextValidYearMonth(String invalidMessage) throws ArgumentValidatorException {
        checkArgIteratorHasNext();
        String value = argIterator.next();
        try {
            return this.dateParseService.parseYearMonth(value);
        } catch (DateTimeParseException e) {
            throw new ArgumentValidatorException("YearMonth is incorrect!", invalidMessage, e);
        }
    }

    public double parseNextValidPositiveDouble(String invalidMessage) throws ArgumentValidatorException {
        checkArgIteratorHasNext();
        String value = argIterator.next();
        try {
            return this.numberParseService.parsePositiveDouble(value);
        } catch (NumberFormatException e) {
            throw new ArgumentValidatorException("double is incorrect!", invalidMessage, e);
        }
    }

    private void checkArgIteratorHasNext() {
        if (!argIterator.hasNext()) {
            throw new RuntimeException("parseNext* methods can't be called when there's no next argument!");
        }
    }
}
