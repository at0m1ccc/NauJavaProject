package ru.tatarinov.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarinov.NauJava.entity.BookCopy;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
}
