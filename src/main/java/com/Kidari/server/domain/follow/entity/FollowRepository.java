package com.Kidari.server.domain.follow.entity;

import com.Kidari.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findAllByMember(Member member);
    Optional<Follow> findByMemberAndBuddyId(Member member, Long id);
}
