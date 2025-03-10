package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) {
        Thread hubEventThread = new Thread(hubEventProcessor);
        hubEventThread.setName("HubEventHandlerThread");
        hubEventThread.start();

        snapshotProcessor.run();
    }
}
