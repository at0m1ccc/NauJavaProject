package ru.tatarinov.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.*;
import ru.tatarinov.NauJava.repository.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LoanServiceTests {
    @Autowired
    private LoanService loanService;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BookCopyRepository bookCopyRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    private Member testMember;
    private BookCopy testBookCopy;

    @BeforeEach
    void setUp() {
        // Создаем автора
        Author author = new Author();
        author.setName("Test Author");
        author.setCountry("Test Country");
        author = authorRepository.save(author);

        // Создаем книгу
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setIsbn("123-4567890123");
        book.setPublicationYear(2023);
        book = bookRepository.save(book);

        // Создаем экземпляр книги
        testBookCopy = new BookCopy();
        testBookCopy.setBook(book);
        testBookCopy.setAvailable(true);
        testBookCopy = bookCopyRepository.save(testBookCopy);

        // Создаем пользователя
        testMember = new Member();
        testMember.setUsername("testuser");
        testMember.setPassword("encodedPassword");
        testMember.setName("Test User");
        testMember.setEmail("test@example.com");
        testMember = memberRepository.save(testMember);
    }

    @Test
    void createLoan_shouldSetBookCopyAsUnavailable() {
        Loan loan = loanService.createLoan(testBookCopy.getId(), testMember.getId());

        assertNotNull(loan.getId());
        assertFalse(bookCopyRepository.findById(testBookCopy.getId()).get().isAvailable());
        assertEquals(LocalDate.now().plusWeeks(2), loan.getDueDate());
    }

    @Test
    void returnLoan_shouldSetBookCopyAsAvailable() {
        Loan loan = loanService.createLoan(testBookCopy.getId(), testMember.getId());
        loanService.returnLoan(loan.getId());

        BookCopy updatedCopy = bookCopyRepository.findById(testBookCopy.getId()).get();
        assertTrue(updatedCopy.isAvailable());
        assertNotNull(loanRepository.findById(loan.getId()).get().getReturnDate());
    }

    @Test
    void getLoansByMemberId_shouldReturnMemberLoans() {
        Loan loan = new Loan();
        loan.setMember(testMember);
        loan.setBookCopy(testBookCopy);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusWeeks(2));
        loanRepository.save(loan);

        List<Loan> loans = loanService.getLoansByMemberId(testMember.getId());
        assertFalse(loans.isEmpty());
        assertEquals(1, loans.size());
        assertEquals(testMember.getId(), loans.get(0).getMember().getId());
    }

    @Test
    void createLoan_withInvalidMemberId_shouldThrowException() {
        assertThrows(EntityNotFoundException.class, () ->
                loanService.createLoan(testBookCopy.getId(), 999L));
    }

    @Test
    void createLoan_withUnavailableCopy_shouldThrowException() {
        testBookCopy.setAvailable(false);
        bookCopyRepository.save(testBookCopy);

        assertThrows(IllegalStateException.class, () ->
                loanService.createLoan(testBookCopy.getId(), testMember.getId()));
    }
}