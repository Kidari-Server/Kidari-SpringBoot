package com.Kidari.server.domain.follow.service;

import com.Kidari.server.common.response.ApiResponse;
import com.Kidari.server.common.response.exception.ErrorCode;
import com.Kidari.server.common.response.exception.FollowException;
import com.Kidari.server.common.validation.ValidationService;
import com.Kidari.server.config.auth.AuthUtils;
import com.Kidari.server.domain.follow.dto.FollowPatchReqDto;
import com.Kidari.server.domain.follow.dto.FollowPatchResDto;
import com.Kidari.server.domain.follow.entity.Follow;
import com.Kidari.server.domain.follow.entity.FollowRepository;
import com.Kidari.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {
    private final AuthUtils authUtils;
    private final ValidationService validationService;
    private final FollowRepository followRepository;

    // 팔로우 관계를 변경함
    public ApiResponse<?> changeFollowStatus(FollowPatchReqDto followPatchReqDto) {
        Member member = authUtils.getMember();
        UUID otherUid = validationService.valMember(followPatchReqDto.nickname()).getUid();
        if (otherUid.equals(member.getUid())) // 사용자 '본인'인 경우
            return ApiResponse.failure(ErrorCode.FOLLOW_BAD_REQUEST);

        // member와 다른 멤버의 uid로 Follow 가져오기
        try {
            Follow follow = getFollowRelation(member, otherUid);
            followRepository.delete(follow); // 해당 팔로우 가져와 삭제
            return ApiResponse.success(new FollowPatchResDto(followPatchReqDto.nickname(), false));
        } catch (FollowException e){
            createFollow(member, otherUid); //새로운 팔로우 생성 및 저장
            return ApiResponse.success(new FollowPatchResDto(followPatchReqDto.nickname(), true));
        }
    }

    // Follow 생성
    public void createFollow(Member member, UUID otherMemberUid){
        Follow follow = Follow.builder()
                .buddyUid(otherMemberUid)
                .member(member)
                .build();
        followRepository.save(follow);
    }

    // TODO: 추후 NPE 확인 필요
    @Transactional(readOnly = true)
    public Follow getFollowRelation(Member member, UUID otherMemberUid){ // 반환값이 NULL일 수 있음
        return followRepository.findByMemberAndBuddyId(member, otherMemberUid)
                .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public boolean followStatus(Member member, UUID otherMemberUid) {
        return followRepository.existsByMemberAndBuddyUid(member, otherMemberUid);
    }

    @Transactional(readOnly = true)
    public List<Follow> findAllFollowByMember(Member member) {
        return followRepository.findAllByMember(member);
    }
}
