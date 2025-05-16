package ru.tatarinov.NauJava.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ru.tatarinov.NauJava.entity.Author;
import ru.tatarinov.NauJava.entity.Book;
import ru.tatarinov.NauJava.repository.AuthorRepository;
import ru.tatarinov.NauJava.repository.BookRepository;
import ru.tatarinov.NauJava.service.BookManagementService;

import java.util.List;

@Service
public class BookManagementServiceImpl implements BookManagementService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public BookManagementServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBookWithAuthor(Book book, Author author) {
        try {
            if (author.getName() == null || author.getName().isBlank()) {
                throw new IllegalArgumentException("Author name cannot be null or empty");
            }

            Author savedAuthor = authorRepository.save(author);
            book.setAuthor(savedAuthor);

            if (book.getTitle() == null || book.getTitle().isBlank()) {
                throw new IllegalArgumentException("Book title cannot be null or empty");
            }

            bookRepository.save(book);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    @Transactional
    public void transferBooksToNewAuthor(Long oldAuthorId, Long newAuthorId) {
        Author newAuthor = authorRepository.findById(newAuthorId)
                .orElseThrow(() -> new IllegalArgumentException("New author not found"));

        List<Book> books = bookRepository.findByAuthorId(oldAuthorId);
        if (books.isEmpty()) {
            throw new IllegalArgumentException("No books found for old author");
        }

        books.forEach(book -> book.setAuthor(newAuthor));
        bookRepository.saveAll(books);
    }
}
