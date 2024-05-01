package com.Kidari.server.domain.follow.service;

import com.Kidari.server.domain.follow.entity.FollowRepository;
import com.Kidari.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {
    private FollowRepository followRepository;

    @Transactional(readOnly = true)
    public boolean followStatus(Member member, UUID otherUid) {
        return followRepository.existsByMemberAndBuddyUid(member, otherUid);
    }
}
