package ru.yandex.practicum.exception;

public class LowQuantityException extends RuntimeException {
    public LowQuantityException(String message) {
        super(message);
    }
}
