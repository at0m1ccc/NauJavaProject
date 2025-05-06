package ru.tatarinov.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.BookCopy;
import ru.tatarinov.NauJava.entity.Loan;
import ru.tatarinov.NauJava.entity.Member;
import ru.tatarinov.NauJava.repository.BookCopyRepository;
import ru.tatarinov.NauJava.repository.LoanRepository;
import ru.tatarinov.NauJava.repository.MemberRepository;
import ru.tatarinov.NauJava.service.LoanService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<Loan> getLoansByMemberId(Long memberId) {
        return loanRepository.findByMemberId(memberId);
    }

    @Override
    @Transactional
    public Loan createLoan(Long bookCopyId, Long memberId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new EntityNotFoundException("Book copy not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        if (!bookCopy.isAvailable()) {
            throw new IllegalStateException("Book copy is not available");
        }

        bookCopy.setAvailable(false);
        bookCopyRepository.save(bookCopy);

        Loan loan = new Loan();
        loan.setBookCopy(bookCopy);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusWeeks(2));

        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public void returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        BookCopy bookCopy = loan.getBookCopy();
        bookCopy.setAvailable(true);
        bookCopyRepository.save(bookCopy);

        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);
    }
}
