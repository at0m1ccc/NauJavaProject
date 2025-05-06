package ru.tatarinov.NauJava.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}
