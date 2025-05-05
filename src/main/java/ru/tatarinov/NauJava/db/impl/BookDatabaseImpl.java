package ru.tatarinov.NauJava.db.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tatarinov.NauJava.db.BookDatabase;
import ru.tatarinov.NauJava.db.BookRepository;
import ru.tatarinov.NauJava.entity.Book;

import java.util.List;
import java.util.Optional;

@Service
public class BookDatabaseImpl implements BookDatabase {
    private final BookRepository bookRepository;

    @Autowired
    public BookDatabaseImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book create(Book book) {
        return bookRepository.insert(book);
    }

    @Override
    public Book update(Book book) {
        return bookRepository.update(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
