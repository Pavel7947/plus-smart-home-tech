package ru.yandex.practicum.telemetry.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.events.hubs.HubEvent;
import ru.yandex.practicum.telemetry.collector.service.EventsHandler;

@RestController
@RequestMapping("/events/hubs")
@Slf4j
@RequiredArgsConstructor
public class HubEventsController {
    private final EventsHandler<HubEvent> hubEventsHandler;

    @PostMapping()
    public void saveHubEvent(@RequestBody HubEvent hubEvent) {
        log.info("Поступил post запрос с событием от хаба с телом: {}", hubEvent);
        hubEventsHandler.save(hubEvent);
    }
}
