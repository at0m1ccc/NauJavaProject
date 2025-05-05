package ru.tatarinov.NauJava.service;

import ru.tatarinov.NauJava.entity.Book;

import java.util.List;
import java.util.Optional;

public interface LibraryService {
    List<Book> getAllBooks();

    Optional<Book> getBookById(Long id);

    Book addBook(Book book);

    Book updateBook(Book book);

    void deleteBook(Long id);

    boolean borrowBook(Long bookId);

    boolean returnBook(Long bookId);

    List<Book> findAvailableBooks();

    List<Book> findByAuthorName(String authorName);
}
