package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.service.AsyncReportService;
import com.example.greenalpinepeaks.service.CounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/concurrency")
@Tag(name = "Concurrency Testing", description = "Демонстрация проблем многопоточности и их решений")
public class ConcurrencyTestController {

    private final CounterService counterService;
    private final AsyncReportService asyncReportService;

    public ConcurrencyTestController(CounterService counterService,
                                     AsyncReportService asyncReportService) {
        this.counterService = counterService;
        this.asyncReportService = asyncReportService;
    }

    @Operation(summary = "Запуск асинхронной генерации отчёта",
        description = "Возвращает ID задачи. Статус можно проверить через /status/{taskId}")
    @PostMapping("/report/start")
    public String startReport() {
        return asyncReportService.startReportGeneration();
    }

    @Operation(summary = "Проверка статуса задачи")
    @GetMapping("/report/{taskId}/status")
    public String getTaskStatus(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable String taskId) {
        return asyncReportService.getTaskStatus(taskId);
    }

    @Operation(summary = "Получение результата задачи")
    @GetMapping("/report/{taskId}/result")
    public String getTaskResult(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable String taskId) {
        String result = asyncReportService.getTaskResult(taskId);
        if (result == null) {
            String status = asyncReportService.getTaskStatus(taskId);
            if ("IN_PROGRESS".equals(status)) {
                return "Задача ещё выполняется...";
            } else if ("NOT_FOUND".equals(status)) {
                return "Задача с таким ID не найдена";
            } else if ("FAILED".equals(status)) {
                return "Задача завершилась с ошибкой";
            }
            return "Результат пока не доступен";
        }
        return result;
    }

    @Operation(summary = "Демонстрация race condition (НЕБЕЗОПАСНЫЙ код)")
    @PostMapping("/race-condition/demo")
    public String demoRaceCondition(
        @Parameter(description = "Количество потоков", example = "50")
        @RequestParam(defaultValue = "50") int threadsCount,
        @Parameter(description = "Количество инкрементов на поток", example = "100")
        @RequestParam(defaultValue = "100") int incrementsPerThread) throws InterruptedException {

        counterService.resetCounters();
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counterService.incrementUnsafe();
                }
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(30, TimeUnit.SECONDS);

        int expected = threadsCount * incrementsPerThread;
        int actual = counterService.getUnsafeCounter();

        if (!finished) {
            executor.shutdownNow();
            return "Тест не завершился в отведённое время";
        }

        return String.format("⚠️ RACE CONDITION (UNSAFE): expected=%d, actual=%d, потеряно обновлений=%d",
            expected, actual, expected - actual);
    }

    @Operation(summary = "Решение race condition через synchronized")
    @PostMapping("/race-condition/synchronized")
    public String demoSynchronized(
        @Parameter(description = "Количество потоков", example = "50")
        @RequestParam(defaultValue = "50") int threadsCount,
        @Parameter(description = "Количество инкрементов на поток", example = "100")
        @RequestParam(defaultValue = "100") int incrementsPerThread) throws InterruptedException {

        counterService.resetCounters();
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counterService.incrementSynchronized();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        int expected = threadsCount * incrementsPerThread;
        int actual = counterService.getSynchronizedCounter();

        return String.format("✅ Решение с synchronized: expected=%d, actual=%d", expected, actual);
    }

    @Operation(summary = "Решение race condition через AtomicInteger")
    @PostMapping("/race-condition/atomic")
    public String demoAtomic(
        @Parameter(description = "Количество потоков", example = "50")
        @RequestParam(defaultValue = "50") int threadsCount,
        @Parameter(description = "Количество инкрементов на поток", example = "100")
        @RequestParam(defaultValue = "100") int incrementsPerThread) throws InterruptedException {

        counterService.resetCounters();
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counterService.incrementAtomic();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        int expected = threadsCount * incrementsPerThread;
        int actual = counterService.getAtomicCounter();

        return String.format("✅ Решение с AtomicInteger: expected=%d, actual=%d", expected, actual);
    }

    @Operation(summary = "Сброс всех счётчиков")
    @PostMapping("/counter/reset")
    public String resetCounters() {
        counterService.resetCounters();
        return "Счётчики сброшены";
    }

    @Operation(summary = "Получить текущие значения счётчиков")
    @GetMapping("/counter/values")
    public String getCounterValues() {
        return String.format(
            "Unsafe (для демонстрации): %d | Synchronized: %d | Atomic: %d",
            counterService.getUnsafeCounter(),
            counterService.getSynchronizedCounter(),
            counterService.getAtomicCounter()
        );
    }
}