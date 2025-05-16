package ru.tatarinov.NauJava.repository.custom;

import ru.tatarinov.NauJava.entity.Book;

import java.util.List;

public interface CustomBookRepository {
    List<Book> findBooksByTitleAndYearRange(String title, Integer startYear, Integer endYear);
    List<Book> findBooksByAuthorCountry(String country);
}
