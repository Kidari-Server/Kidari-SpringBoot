package com.Kidari.server.domain.member.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByUid(UUID uid);
    Optional<Member> findByLogin(String login);
    Optional<Member> findBySub(String sub);
}
