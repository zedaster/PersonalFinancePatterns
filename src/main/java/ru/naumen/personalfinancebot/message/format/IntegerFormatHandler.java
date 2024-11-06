package ru.naumen.personalfinancebot.message.format;

public class IntegerFormatHandler implements FormatHandler {

    private final int value;

    public IntegerFormatHandler(int value) {
        this.value = value;
    }

    @Override
    public String handleString() {
        return String.valueOf(value);
    }
}
