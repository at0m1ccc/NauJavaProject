package ru.tatarinov.NauJava.service;

import ru.tatarinov.NauJava.entity.Member;
import ru.tatarinov.NauJava.entity.dto.MemberRegistrationDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Member registerNewMember(MemberRegistrationDto registrationDto);
    Optional<Member> findByUsername(String username);
    List<Member> getAllMembers();
    void deleteMember(Long id);
}