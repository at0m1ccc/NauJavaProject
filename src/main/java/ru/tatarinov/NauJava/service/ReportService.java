package ru.tatarinov.NauJava.service;

import ru.tatarinov.NauJava.entity.Report;

import java.util.concurrent.CompletableFuture;

public interface ReportService {
    Long createReport();
    Report getReport(Long id);
    CompletableFuture<Void> generateReport(Long reportId);
}
