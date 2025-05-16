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
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    @Async
    @Override
    public CompletableFuture<Void> generateReport(Long reportId) {
        long startTotalTime = System.currentTimeMillis();

        return CompletableFuture.runAsync(() -> {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new IllegalArgumentException("Report not found"));

            try {
                CompletableFuture<Long> usersCountFuture = CompletableFuture.supplyAsync(() -> {
                    long startTime = System.currentTimeMillis();
                    long count = memberRepository.count();
                    report.setUsersCountTime(System.currentTimeMillis() - startTime);
                    return count;
                });

                CompletableFuture<List<Book>> booksFuture = CompletableFuture.supplyAsync(() -> {
                    long startTime = System.currentTimeMillis();
                    List<Book> books = bookRepository.findAllWithAuthors();
                    report.setBooksListTime(System.currentTimeMillis() - startTime);
                    return books;
                });

                CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(usersCountFuture, booksFuture)
                        .thenRun(() -> {
                            try {
                                long usersCount = usersCountFuture.get();
                                List<Book> books = booksFuture.get();
                                long totalTime = System.currentTimeMillis() - startTotalTime;

                                String htmlContent = buildHtmlReport(
                                        usersCount, report.getUsersCountTime(),
                                        books, report.getBooksListTime(),
                                        totalTime
                                );

                                report.setContent(htmlContent);
                                report.setStatus(ReportStatus.COMPLETED);
                                reportRepository.save(report);
                            } catch (Exception e) {
                                throw new CompletionException(e);
                            }
                        });

                combinedFuture.join();

            } catch (Exception e) {
                log.error("Error generating report", e);
                report.setStatus(ReportStatus.ERROR);
                report.setContent("Error generating report: " + e.getMessage());
                reportRepository.save(report);
            }
        });
    }

    private String buildHtmlReport(long usersCount, long usersTime,
                                   List<Book> books, long booksTime,
                                   long totalTime) {
        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <title>Library Report</title>
                            <style>
                                table { border-collapse: collapse; width: 100%%; }
                                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                                th { background-color: #f2f2f2; }
                                .time { text-align: right; }
                            </style>
                        </head>
                        <body>
                            <h1>Library Statistics Report</h1>
                            <table>
                                <tr><th>Metric</th><th>Value</th><th>Time (ms)</th></tr>
                                <tr>
                                    <td>Registered users</td>
                                    <td>%d</td>
                                    <td class="time">%d</td>
                                </tr>
                                <tr>
                                    <td>Books count</td>
                                    <td>%d</td>
                                    <td class="time">%d</td>
                                </tr>
                                <tr>
                                    <td colspan="2"><strong>Total generation time</strong></td>
                                    <td class="time"><strong>%d</strong></td>
                                </tr>
                                <tr><td colspan="3"><h3>Books:</h3><ul>%s</ul></td></tr>
                            </table>
                        </body>
                        </html>
                        """,
                usersCount, usersTime,
                books.size(), booksTime,
                totalTime,
                books.stream()
                        .map(b -> String.format("<li>%s by %s (%d)</li>",
                                b.getTitle(),
                                b.getAuthor() != null ? b.getAuthor().getName() : "Unknown",
                                b.getPublicationYear()))
                        .collect(Collectors.joining()));
    }
}