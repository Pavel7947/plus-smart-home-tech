package ru.yandex.practicum.telemetry.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.events.sensors.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.EventsHandler;

@RestController
@RequestMapping("/events/sensors")
@Slf4j
@RequiredArgsConstructor
public class SensorEventsController {
    private final EventsHandler<SensorEvent> sensorEventsHandler;

    @PostMapping()
    public void saveSensorEvent(@RequestBody SensorEvent sensorEvent) {
        log.info("Поступил post запрос с событием от датчиков с телом: {}", sensorEvent);
        sensorEventsHandler.save(sensorEvent);
    }
}
