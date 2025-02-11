package ru.yandex.practicum.telemetry.collector.service;

public interface EventsHandler<T> {

    void save(T hubEvent);
}
