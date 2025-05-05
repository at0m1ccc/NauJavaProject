package ru.tatarinov.NauJava.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tatarinov.NauJava.db.BookDatabase;
import ru.tatarinov.NauJava.entity.Book;
import ru.tatarinov.NauJava.service.LibraryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibraryServiceImpl implements LibraryService {
    private final BookDatabase bookDatabase;

    @Autowired
    public LibraryServiceImpl(BookDatabase bookDatabase) {
        this.bookDatabase = bookDatabase;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookDatabase.findAll();
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookDatabase.findById(id);
    }

    @Override
    public Book addBook(Book book) {
        if (book.getId() != null) {
            throw new IllegalArgumentException("New book must not have an ID");
        }
        return bookDatabase.create(book);
    }

    @Override
    public Book updateBook(Book book) {
        if (book.getId() == null) {
            throw new IllegalArgumentException("Book ID must not be null");
        }
        return bookDatabase.update(book);
    }

    @Override
    public void deleteBook(Long id) {
        if (bookDatabase.findById(id).isEmpty()) {
            return;
        }
        bookDatabase.deleteById(id);
    }

    @Override
    public boolean borrowBook(Long bookId) {
        return bookDatabase.findById(bookId)
                .filter(Book::isAvailable)
                .map(book -> {
                    book.setAvailable(false);
                    bookDatabase.update(book);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean returnBook(Long bookId) {
        return bookDatabase.findById(bookId)
                .filter(book -> !book.isAvailable())
                .map(book -> {
                    book.setAvailable(true);
                    bookDatabase.update(book);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<Book> findAvailableBooks() {
        return bookDatabase.findAll().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookDatabase.findAll().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }
}
