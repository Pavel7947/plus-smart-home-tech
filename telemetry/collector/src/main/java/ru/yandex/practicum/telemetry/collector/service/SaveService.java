package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.telemetry.collector.events.hubs.HubEvent;
import ru.yandex.practicum.telemetry.collector.events.sensors.SensorEvent;

public interface SaveService {

    void save(SensorEvent sensorEvent);

    void save(HubEvent hubEvent);
}
