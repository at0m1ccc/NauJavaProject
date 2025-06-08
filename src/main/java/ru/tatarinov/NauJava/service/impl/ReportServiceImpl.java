package ru.tatarinov.NauJava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.Book;
import ru.tatarinov.NauJava.entity.Report;
import ru.tatarinov.NauJava.enums.ReportStatus;
import ru.tatarinov.NauJava.repository.BookRepository;
import ru.tatarinov.NauJava.repository.MemberRepository;
import ru.tatarinov.NauJava.repository.ReportRepository;
import ru.tatarinov.NauJava.service.ReportService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    private final Executor executor = Executors.newFixedThreadPool(2);

    @Override
    @Transactional
    public Long createReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        report.setContent("Report is being generated...");
        return reportRepository.save(report).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Report getReport(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    }

    @Override
    @Async
    public CompletableFuture<Void> generateReport(Long reportId) {
        long startTotalTime = System.currentTimeMillis(); // Фиксируем начало генерации

        return CompletableFuture.runAsync(() -> {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new IllegalArgumentException("Report not found"));

            try {
                long startUsersTime = System.currentTimeMillis();
                long usersCount = memberRepository.count();
                long usersTime = System.currentTimeMillis() - startUsersTime;

                long startBooksTime = System.currentTimeMillis();
                List<Book> books = bookRepository.findAll();
                long booksTime = System.currentTimeMillis() - startBooksTime;

                String htmlContent = buildHtmlContent(usersCount, usersTime,
                        books, booksTime,
                        System.currentTimeMillis() - startTotalTime);

                report.setContent(htmlContent);
                report.setStatus(ReportStatus.COMPLETED);
                reportRepository.save(report);

            } catch (Exception e) {
                log.error("Error generating report", e);
                report.setStatus(ReportStatus.ERROR);
                report.setContent("Error generating report: " + e.getMessage());
                reportRepository.save(report);
            }
        });
    }

    private String buildHtmlContent(long usersCount, long usersTime,
                                    List<Book> books, long booksTime,
                                    long totalTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>")
                .append("<html><head><title>Library Report</title>")
                .append("<style>table {border-collapse: collapse; width: 100%;} ")
                .append("th, td {border: 1px solid #ddd; padding: 8px; text-align: left;} ")
                .append("th {background-color: #f2f2f2;}</style></head>")
                .append("<body><h1>Library Statistics Report</h1>")
                .append("<table>")
                .append("<tr><th>Metric</th><th>Value</th><th>Time (ms)</th></tr>")
                .append(String.format("<tr><td>Registered users</td><td>%d</td><td>%d</td></tr>",
                        usersCount, usersTime))
                .append(String.format("<tr><td>Books count</td><td>%d</td><td>%d</td></tr>",
                        books.size(), booksTime))
                .append(String.format("<tr><td>Total generation time</td><td></td><td>%d</td></tr>",
                        totalTime))
                .append("<tr><td colspan='3'><h3>Books:</h3><ul>");

        books.forEach(book -> {
            String authorName = book.getAuthor() != null ? book.getAuthor().getName() : "Unknown";
            sb.append(String.format("<li>%s by %s (%d)</li>",
                    book.getTitle(), authorName, book.getPublicationYear()));
        });

        sb.append("</ul></td></tr>")
                .append("</table></body></html>");

        return sb.toString();
    }
}