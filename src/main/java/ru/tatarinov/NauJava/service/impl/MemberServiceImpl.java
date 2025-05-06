package ru.tatarinov.NauJava.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tatarinov.NauJava.entity.Member;
import ru.tatarinov.NauJava.entity.dto.MemberRegistrationDto;
import ru.tatarinov.NauJava.repository.MemberRepository;
import ru.tatarinov.NauJava.service.MemberService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Member registerNewMember(MemberRegistrationDto registrationDto) {
        Member member = new Member();
        member.setUsername(registrationDto.getUsername());
        member.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        member.setRoles(Set.of("USER"));
        member.setName(registrationDto.getName());
        member.setEmail(registrationDto.getEmail());
        member.setPhone(registrationDto.getPhone());

        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
