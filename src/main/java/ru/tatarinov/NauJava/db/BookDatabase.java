package ru.tatarinov.NauJava.db;

import ru.tatarinov.NauJava.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookDatabase {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    Book create(Book book);

    Book update(Book book);

    void deleteById(Long id);
}
