package ru.tatarinov.NauJava.repository.custom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.tatarinov.NauJava.entity.Book;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@Import(CustomBookRepositoryImpl.class)
@Sql(scripts = "/test-data.sql")
class CustomBookRepositoryTests {

    @Autowired
    private CustomBookRepository customBookRepository;

    @Test
    void findBooksByTitleAndYearRange_shouldReturnBooks() {
        List<Book> result = customBookRepository.findBooksByTitleAndYearRange(
                "Spring", 2019, 2021);

        assertFalse(result.isEmpty());
        assertEquals("Spring in Action", result.getFirst().getTitle());
    }

    @Test
    void findBooksByAuthorCountry_shouldReturnBooks() {
        List<Book> result = customBookRepository.findBooksByAuthorCountry("USA");
        assertFalse(result.isEmpty());
        assertEquals("Effective Java", result.getFirst().getTitle());
    }
}