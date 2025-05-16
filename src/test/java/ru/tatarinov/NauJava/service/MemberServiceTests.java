package ru.tatarinov.NauJava.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.Member;
import ru.tatarinov.NauJava.entity.dto.MemberRegistrationDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberServiceTests {
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerNewMember_shouldSaveMemberWithEncodedPassword() {
        MemberRegistrationDto dto = new MemberRegistrationDto();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setName("Test User");

        Member member = memberService.registerNewMember(dto);

        assertNotNull(member.getId());
        assertTrue(passwordEncoder.matches("password123", member.getPassword()));
        assertEquals(1, member.getRoles().size());
        assertTrue(member.getRoles().contains("USER"));
    }

    @Test
    void findByUsername_shouldReturnMember() {
        MemberRegistrationDto dto = new MemberRegistrationDto();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setName("Test User");
        memberService.registerNewMember(dto);

        Optional<Member> found = memberService.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void deleteMember_shouldRemoveMember() {
        MemberRegistrationDto dto = createValidDto();
        Member member = memberService.registerNewMember(dto);

        memberService.deleteMember(member.getId());

        Optional<Member> deleted = memberService.findByUsername("testuser");
        assertTrue(deleted.isEmpty());
    }

    @Test
    void registerNewMember_withExistingUsername_shouldThrowException() {
        MemberRegistrationDto dto1 = createValidDto();
        memberService.registerNewMember(dto1);

        MemberRegistrationDto dto2 = createValidDto();
        dto2.setEmail("another@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            memberService.registerNewMember(dto2);
        });
    }

    @Test
    void registerNewMember_withNullUsername_shouldThrowException() {
        MemberRegistrationDto dto = createValidDto();
        dto.setUsername(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            memberService.registerNewMember(dto);
        });
    }

    @Test
    void findByUsername_withNonExistingUser_shouldReturnEmpty() {
        Optional<Member> found = memberService.findByUsername("nonexistent");
        assertTrue(found.isEmpty());
    }

    private MemberRegistrationDto createValidDto() {
        MemberRegistrationDto dto = new MemberRegistrationDto();
        dto.setUsername("testuser");
        dto.setPassword("ValidPass123!");
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        dto.setPhone("+1234567890");
        return dto;
    }
}
