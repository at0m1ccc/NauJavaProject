package ru.tatarinov.NauJava.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tatarinov.NauJava.entity.Book;
import ru.tatarinov.NauJava.entity.dto.BookDto;
import ru.tatarinov.NauJava.repository.BookRepository;

import java.util.List;

@RestController
@RequestMapping("/api/custom/books")
@RequiredArgsConstructor
public class BookCustomController {
    private final BookRepository bookRepository;

    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(
            @RequestParam String title,
            @RequestParam int startYear,
            @RequestParam int endYear) {

        List<Book> books = bookRepository.findByTitleContainingAndPublicationYearBetween(title, startYear, endYear);
        List<BookDto> bookDtos = books.stream()
                .map(this::convertToDto)
                .toList();

        return ResponseEntity.ok(bookDtos);
    }

    @GetMapping("/by-country/{country}")
    public ResponseEntity<List<BookDto>> getByCountry(@PathVariable String country) {
        List<Book> books = bookRepository.findByAuthorCountry(country);
        List<BookDto> bookDtos = books.stream()
                .map(this::convertToDto)
                .toList();

        return ResponseEntity.ok(bookDtos);
    }

    private BookDto convertToDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor() != null ? book.getAuthor().getName() : null,
                book.getAuthor() != null ? book.getAuthor().getCountry() : null,
                book.getPublicationYear(),
                book.getIsbn()
        );
    }
}