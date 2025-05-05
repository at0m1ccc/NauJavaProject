package ru.tatarinov.NauJava.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tatarinov.NauJava.entity.Book;
import ru.tatarinov.NauJava.repository.BookRepository;

import java.util.List;

@RestController
@RequestMapping("/api/custom/books")
public class BookCustomController {
    private final BookRepository bookRepository;

    @Autowired
    public BookCustomController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam String title,
            @RequestParam int startYear,
            @RequestParam int endYear) {
        return ResponseEntity.ok(
                bookRepository.findByTitleContainingAndPublicationYearBetween(title, startYear, endYear)
        );
    }

    @GetMapping("/by-country/{country}")
    public ResponseEntity<List<Book>> getByCountry(@PathVariable String country) {
        return ResponseEntity.ok(
                bookRepository.findByAuthorCountry(country)
        );
    }
}
