package com.Kidari.server.domain.commit.entity;

import com.Kidari.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    Optional<Commit> findByMember(Member member);
}
