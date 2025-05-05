package ru.tatarinov.NauJava.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.Author;
import ru.tatarinov.NauJava.entity.Book;
import ru.tatarinov.NauJava.entity.BookCopy;
import ru.tatarinov.NauJava.repository.AuthorRepository;
import ru.tatarinov.NauJava.repository.BookCopyRepository;
import ru.tatarinov.NauJava.repository.BookRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/test-data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookManagementServiceTests {

    @Autowired
    private BookManagementService bookManagementService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void clearContext() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void addBookWithAuthor_shouldSaveBothEntities() {
        Author newAuthor = new Author();
        newAuthor.setName("New Author");
        newAuthor.setCountry("Russia");

        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setPublicationYear(2023);

        BookCopy copy = new BookCopy();
        copy.setAvailable(true);
        newBook.getCopies().add(copy);
        copy.setBook(newBook);

        bookManagementService.addBookWithAuthor(newBook, newAuthor);

        Author savedAuthor = authorRepository.findByName("New Author");
        assertNotNull(savedAuthor);

        List<Book> books = bookRepository.findByAuthorId(savedAuthor.getId());
        assertFalse(books.isEmpty());
        assertEquals("New Book", books.getFirst().getTitle());
        assertFalse(books.getFirst().getCopies().isEmpty());
    }

    @Test
    void transferBooksToNewAuthor_shouldTransferBooks() {
        Author oldAuthor = authorRepository.findByName("Joshua Bloch");

        Author newAuthor = new Author();
        newAuthor.setName("New Author");
        newAuthor.setCountry("USA");
        authorRepository.save(newAuthor);

        bookManagementService.transferBooksToNewAuthor(oldAuthor.getId(), newAuthor.getId());

        List<Book> transferredBooks = bookRepository.findByAuthorId(newAuthor.getId());
        assertFalse(transferredBooks.isEmpty());
        assertEquals("Effective Java", transferredBooks.getFirst().getTitle());
        assertEquals(newAuthor.getId(), transferredBooks.getFirst().getAuthor().getId());
    }

    @Test
    void transferBooksToNewAuthor_whenNewAuthorNotFound_shouldThrowException() {
        Author oldAuthor = authorRepository.findByName("Joshua Bloch");
        assertNotNull(oldAuthor, "Test author 'Joshua Bloch' should exist");

        assertThrows(IllegalArgumentException.class, () ->
                bookManagementService.transferBooksToNewAuthor(oldAuthor.getId(), 999L));

        List<Book> books = bookRepository.findByAuthorId(oldAuthor.getId());
        assertFalse(books.isEmpty(), "Books should remain with original author");
        assertEquals("Effective Java", books.getFirst().getTitle());
    }

    @Test
    void addBookWithAuthor_whenAuthorInvalid_shouldRollback() {
        long initialAuthorCount = authorRepository.count();
        long initialBookCount = bookRepository.count();
        long initialCopyCount = bookCopyRepository.count();

        Author invalidAuthor = new Author();
        invalidAuthor.setCountry("Some Country");

        Book book = new Book();
        book.setTitle("Should Not Be Saved");
        book.setPublicationYear(2023);

        BookCopy copy = new BookCopy();
        copy.setAvailable(true);
        book.getCopies().add(copy);
        copy.setBook(book);

        assertThrows(IllegalArgumentException.class, () ->
                bookManagementService.addBookWithAuthor(book, invalidAuthor));

        assertEquals(initialAuthorCount, authorRepository.count(),
                "Количество авторов не должно измениться");
        assertEquals(initialBookCount, bookRepository.count(),
                "Количество книг не должно измениться");
        assertEquals(initialCopyCount, bookCopyRepository.count(),
                "Количество копий не должно измениться");

        assertTrue(bookRepository.findByTitle("Should Not Be Saved").isEmpty());
        assertNull(authorRepository.findByName(null));
    }

    @Test
    void findByAuthorName_shouldReturnBooks() {
        List<Book> books = bookRepository.findByAuthorName("Joshua Bloch");

        assertFalse(books.isEmpty());
        assertEquals("Effective Java", books.getFirst().getTitle());
        assertEquals("Joshua Bloch", books.getFirst().getAuthor().getName());
    }

    @Test
    void findByTitle_shouldReturnBook() {
        List<Book> books = bookRepository.findByTitle("Effective Java");

        assertFalse(books.isEmpty());
        assertEquals("Effective Java", books.getFirst().getTitle());
        assertEquals("Joshua Bloch", books.getFirst().getAuthor().getName());
    }

    @Test
    void findByTitle_whenNotExists_shouldReturnEmptyList() {
        List<Book> books = bookRepository.findByTitle("Nonexistent Book");
        assertTrue(books.isEmpty());
    }
}
