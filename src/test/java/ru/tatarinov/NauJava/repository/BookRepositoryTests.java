package ru.tatarinov.NauJava.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.tatarinov.NauJava.entity.Author;
import ru.tatarinov.NauJava.entity.Book;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "/test-data.sql")
class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void findByTitleContainingAndPublicationYearBetween_shouldReturnBooks() {
        List<Book> result = bookRepository.findByTitleContainingAndPublicationYearBetween(
                "Spring", 2019, 2021);

        assertFalse(result.isEmpty());
        assertEquals("Spring in Action", result.getFirst().getTitle());
    }

    @Test
    void findByAuthorCountry_shouldReturnBooks() {
        List<Book> result = bookRepository.findByAuthorCountry("USA");
        assertFalse(result.isEmpty());
        assertEquals("Effective Java", result.getFirst().getTitle());
    }

    @Test
    void findByAuthorId_shouldReturnBooks() {
        Author author = authorRepository.findAll().getFirst();
        List<Book> result = bookRepository.findByAuthorId(author.getId());

        assertFalse(result.isEmpty());
        assertEquals(author.getId(), result.getFirst().getAuthor().getId());
    }

    @Test
    void findByAuthorName_shouldReturnBooks() {
        List<Book> result = bookRepository.findByAuthorName("Joshua Bloch");
        assertFalse(result.isEmpty());
        assertEquals("Effective Java", result.getFirst().getTitle());
    }
}
