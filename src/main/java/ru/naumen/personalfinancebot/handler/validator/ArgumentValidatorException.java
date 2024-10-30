package ru.naumen.personalfinancebot.handler.validator;

public class ArgumentValidatorException extends Exception {
    private final String invalidMessage;

    public ArgumentValidatorException(String message, String invalidMessage) {
        super(message);
        this.invalidMessage = invalidMessage;
    }

    public ArgumentValidatorException(String message, String invalidMessage, Throwable cause) {
        super(message, cause);
        this.invalidMessage = invalidMessage;
    }

    public String getInvalidMessage() {
        return invalidMessage;
    }
}
