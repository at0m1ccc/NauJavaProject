package ru.tatarinov.NauJava.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.Author;
import ru.tatarinov.NauJava.entity.Book;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class CustomBookRepositoryImpl implements CustomBookRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findBooksByTitleAndYearRange(String title, Integer startYear, Integer endYear) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);

        Predicate titlePredicate = cb.like(book.get("title"), "%" + title + "%");
        Predicate yearPredicate = cb.between(book.get("publicationYear"), startYear, endYear);

        query.where(cb.and(titlePredicate, yearPredicate));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Book> findBooksByAuthorCountry(String country) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        Join<Book, Author> author = book.join("author");

        query.where(cb.equal(author.get("country"), country));
        return entityManager.createQuery(query).getResultList();
    }
}
