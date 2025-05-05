package ru.tatarinov.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.tatarinov.NauJava.entity.Book;

import java.util.List;

@RepositoryRestResource(path = "books")
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Метод поиска по названию и году публикации (использует Query Lookup Strategies)
    List<Book> findByTitleContainingAndPublicationYearBetween(String title, Integer startYear, Integer endYear);

    // Метод поиска книг по стране автора (использует JPQL)
    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.country = :country")
    List<Book> findByAuthorCountry(String country);

    // Метод поиска книг по ID автора
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId")
    List<Book> findByAuthorId(Long authorId);

    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.name = :authorName")
    List<Book> findByAuthorName(String authorName);

    @Query("SELECT b FROM Book b WHERE b.title = :title")
    List<Book> findByTitle(String title);
}
