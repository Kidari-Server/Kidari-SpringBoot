package com.Kidari.server.domain.follow.entity;

import com.Kidari.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    Boolean existsByMemberAndBuddyUid(Member member, UUID buddyUid);
}
