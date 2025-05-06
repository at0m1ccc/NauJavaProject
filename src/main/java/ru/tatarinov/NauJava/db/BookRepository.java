package ru.tatarinov.NauJava.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tatarinov.NauJava.entity.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BookRepository {
    private final ArrayList<Book> books;
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Autowired
    public BookRepository(ArrayList<Book> bookContainer) {
        books = bookContainer;
        // Инициализация тестовыми данными
        insert(new Book(null, "Война и мир", "Лев Толстой", true, "978-5-389-06256-6"));
        insert(new Book(null, "Преступление и наказание", "Фёдор Достоевский", true, "978-5-17-090831-7"));
    }

    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    public Optional<Book> findById(Long id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }

    public Book insert(Book book) {
        if (book.getId() != null) {
            throw new IllegalArgumentException("New book must not have an ID");
        }

        Book newBook = new Book(
                idGenerator.getAndIncrement(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable(),
                book.getIsbn()
        );

        books.add(newBook);
        return newBook;
    }

    public Book update(Book book) {
        if (book.getId() == null) {
            throw new IllegalArgumentException("Book ID must not be null");
        }

        int index = -1;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(book.getId())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Book with ID " + book.getId() + " not found");
        }

        Book updatedBook = new Book(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable(),
                book.getIsbn()
        );

        books.set(index, updatedBook);
        return updatedBook;
    }

    public void deleteById(Long id) {
        books.removeIf(book -> book.getId().equals(id));
    }
}