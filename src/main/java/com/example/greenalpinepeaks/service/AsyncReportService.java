package com.example.greenalpinepeaks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AsyncReportService {

    private static final Logger LOG =
        LoggerFactory.getLogger(AsyncReportService.class);

    private final Map<String, String> taskStatus =
        new ConcurrentHashMap<>();

    private final Map<String, String> taskResults =
        new ConcurrentHashMap<>();

    private final AsyncReportService self;

    public AsyncReportService(@Lazy AsyncReportService self) {
        this.self = self;
    }

    public String startReportGeneration() {

        String taskId = UUID.randomUUID().toString();

        taskStatus.put(taskId, "IN_PROGRESS");

        LOG.info(
            "Задача {} поставлена в очередь на выполнение",
            taskId
        );

        self.processReportAsync(taskId);

        return taskId;
    }

    @Async("taskExecutor")
    public void processReportAsync(String taskId) {

        LOG.info(
            "Асинхронная обработка задачи: {} в потоке {}",
            taskId,
            Thread.currentThread().getName()
        );

        try {

            Thread.sleep(12000);

            String result = String.format(
                "Отчёт для задачи %s сгенерирован в %s",
                taskId,
                LocalDateTime.now()
            );

            taskResults.put(taskId, result);

            taskStatus.put(taskId, "COMPLETED");

            LOG.info(
                "Задача {} успешно завершена",
                taskId
            );

        } catch (InterruptedException e) {

            taskStatus.put(taskId, "FAILED");

            Thread.currentThread().interrupt();

            LOG.error(
                "Задача {} была прервана",
                taskId,
                e
            );

            throw new RuntimeException(
                "Асинхронная задача прервана",
                e
            );
        }
    }

    public String getTaskStatus(String taskId) {
        return taskStatus.getOrDefault(taskId, "NOT_FOUND");
    }

    public String getTaskResult(String taskId) {
        return taskResults.get(taskId);
    }

    public int getQueueSize() {
        return (int) taskStatus.values().stream()
            .filter(status -> status.equals("IN_PROGRESS"))
            .count();
    }

    public int getCompletedCount() {
        return (int) taskStatus.values().stream()
            .filter(status -> status.equals("COMPLETED"))
            .count();
    }

    public int getFailedCount() {
        return (int) taskStatus.values().stream()
            .filter(status -> status.equals("FAILED"))
            .count();
    }
}