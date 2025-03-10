package ru.yandex.practicum.telemetry.analyzer.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
