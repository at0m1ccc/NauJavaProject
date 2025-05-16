package ru.tatarinov.NauJava.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.Book;
import ru.tatarinov.NauJava.entity.BookCopy;
import ru.tatarinov.NauJava.repository.BookRepository;
import ru.tatarinov.NauJava.service.LibraryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class LibraryServiceImpl implements LibraryService {
    private final BookRepository bookRepository;

    @Autowired
    public LibraryServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional
    public Book addBook(Book book) {
        if (book.getId() != null) {
            throw new IllegalArgumentException("New book must not have an ID");
        }

        if (book.getCopies().isEmpty()) {
            BookCopy copy = new BookCopy();
            copy.setBook(book);
            copy.setAvailable(true);
            book.getCopies().add(copy);
        }

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Book book) {
        if (book.getId() == null) {
            throw new IllegalArgumentException("Book ID must not be null");
        }
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean borrowBook(Long bookId) {
        return bookRepository.findById(bookId)
                .flatMap(book -> book.getCopies().stream()
                        .filter(BookCopy::isAvailable)
                        .findFirst()
                        .map(copy -> {
                            copy.setAvailable(false);
                            return true;
                        }))
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean returnBook(Long bookId) {
        return bookRepository.findById(bookId)
                .flatMap(book -> book.getCopies().stream()
                        .filter(copy -> !copy.isAvailable())
                        .findFirst()
                        .map(copy -> {
                            copy.setAvailable(true);
                            return true;
                        }))
                .orElse(false);
    }

    @Override
    public List<Book> findAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> book.getCopies().stream()
                        .anyMatch(BookCopy::isAvailable))
                .toList();
    }

    @Override
    public List<Book> findByAuthorName(String authorName) {
        return bookRepository.findByAuthorName(authorName);
    }
}
