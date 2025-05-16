package ru.tatarinov.NauJava.service;

import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.Author;
import ru.tatarinov.NauJava.entity.Book;

public interface BookManagementService {
    @Transactional
    void addBookWithAuthor(Book book, Author author);

    @Transactional
    void transferBooksToNewAuthor(Long oldAuthorId, Long newAuthorId);
}
