package ru.tatarinov.NauJava.service;

import ru.tatarinov.NauJava.entity.Loan;

import java.util.List;

public interface LoanService {
    List<Loan> getLoansByMemberId(Long memberId);
    Loan createLoan(Long bookCopyId, Long memberId);
    void returnLoan(Long loanId);
}