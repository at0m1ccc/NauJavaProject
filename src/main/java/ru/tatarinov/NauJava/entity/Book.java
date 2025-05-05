package ru.tatarinov.NauJava.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Book {
    private Long id;
    private String title;
    private String author;
    private boolean isAvailable;
    private String isbn;

    public Book() {
    }

    public Book(Long id, String title, String author, boolean isAvailable, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isAvailable=" + isAvailable +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}