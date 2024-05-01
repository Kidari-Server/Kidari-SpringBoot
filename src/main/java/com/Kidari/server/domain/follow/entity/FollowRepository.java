package com.Kidari.server.domain.follow.entity;

import com.Kidari.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    Follow findByMemberAndBuddyId(Member member, UUID buddyUid);
    Boolean existsByMemberAndBuddyUid(Member member, UUID buddyUid);
    List<Follow> findAllByMember(Member member);
}
