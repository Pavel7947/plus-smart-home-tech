package ru.yandex.practicum.telemetry.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.events.hubs.HubEvent;
import ru.yandex.practicum.telemetry.collector.events.sensors.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.SaveService;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class EventsController {
    private final SaveService saveService;

    @PostMapping("/sensors")
    public void saveSensorEvent(@RequestBody SensorEvent sensorEvent) {
        log.info("Поступил post запрос с событием от датчиков с телом: {}", sensorEvent);
        saveService.save(sensorEvent);
    }

    @PostMapping("/hubs")
    public void saveHubEvent(@RequestBody HubEvent hubEvent) {
        log.info("Поступил post запрос с событием от хаба с телом: {}", hubEvent);
        saveService.save(hubEvent);
    }

}
