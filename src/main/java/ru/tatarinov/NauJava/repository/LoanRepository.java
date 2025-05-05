package ru.tatarinov.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarinov.NauJava.entity.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
}
