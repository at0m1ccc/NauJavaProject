package ru.tatarinov.NauJava.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tatarinov.NauJava.entity.Report;
import ru.tatarinov.NauJava.enums.ReportStatus;
import ru.tatarinov.NauJava.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Long> createReport() {
        Long reportId = reportService.createReport();
        reportService.generateReport(reportId);
        return ResponseEntity.ok(reportId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getReport(@PathVariable Long id) {
        Report report = reportService.getReport(id);

        if (report.getStatus() == ReportStatus.COMPLETED) {
            return ResponseEntity.ok(report.getContent());
        } else if (report.getStatus() == ReportStatus.ERROR) {
            return ResponseEntity.badRequest().body("Report generation failed: " + report.getContent());
        } else {
            return ResponseEntity.accepted().body("Report is still being generated");
        }
    }
}
