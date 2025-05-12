package ru.tatarinov.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.tatarinov.NauJava.entity.Author;

@RepositoryRestResource(path = "author")
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByName(String name);
}
