package ru.tatarinov.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tatarinov.NauJava.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
