package com.Kidari.server.domain.member.service;

import com.Kidari.server.common.response.ApiResponse;
import com.Kidari.server.common.response.exception.ErrorCode;
import com.Kidari.server.common.response.exception.MemberException;
import com.Kidari.server.common.validation.ValidationService;
import com.Kidari.server.config.auth.AuthUtils;
import com.Kidari.server.domain.follow.service.FollowService;
import com.Kidari.server.domain.member.dto.MemberHomeResDto;
import com.Kidari.server.domain.member.dto.MemberInfoResDto;
import com.Kidari.server.domain.member.dto.MemberSearchResDto;
import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.entity.MemberRepository;
import com.Kidari.server.domain.univ.entity.Univ;
import com.Kidari.server.domain.univ.entity.UnivRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final ValidationService validationService;
    private final FollowService followService;
    private final MemberRepository memberRepository;
    private final UnivRepository univRepository;
    private final AuthUtils authUtils;

    // 본인 정보 조회
    public ApiResponse<?> getHomeInfo() {
        Member member = authUtils.getMember();
        return ApiResponse.success(new MemberHomeResDto(member));
    }

    // 유저를 nickname으로 검색, 내가 follow중인 상태인지와 함께 보냄
    public ApiResponse<?> searchMember(String nickname) {
        List<Member> searchedMembers = memberRepository.findByLoginStartingWith(nickname);
        if (searchedMembers == null)
            return ApiResponse.failure(ErrorCode.MEMBER_NOT_FOUND); // ErrorCode 추가 필요
        List<MemberSearchResDto> resDtos = searchedMembers.stream()
                .map(member -> new MemberSearchResDto(member, followService.followStatus(member)))
                .collect(Collectors.toList());
        return ApiResponse.success();
    }

    // 다른 단일 유저 정보 조회
    public ApiResponse<?> getMemberInfo(String nickname){
        Member buddy = validationService.valMember(nickname);
        Boolean followStatus = followService.followStatus(buddy);
        return ApiResponse.success(new MemberInfoResDto(buddy, followStatus)); // 팔로우 상태와 함께 보냄
    }

    // 내 알림을 확인된 상태로 변경
    public Member checkNewAlarm() {
        Member member = authUtils.getMember();
        member.alarmChecked();
        return memberRepository.save(member);
    }

    // 내 눈사람 키 키우기. 눈송이 사용에 실패한 경우 MemberException
    public Member growSnowman() throws MemberException {
        Member member = authUtils.getMember();
        member.useSnowflake(); // 눈송이 사용에 실패한 경우 MemberException
        return refreshHeight(member, 1L);
    }

    // 단일 멤버의 눈사람 키 갱신 (멤버와 Univ의 totalHeight), diff는 양수(키우기) 또는 음수(공격받음)
    public Member refreshHeight(Member member, Long diff) {
        // Univ의 totalHeight 갱신
        Univ univ = member.getUniv();
        univ.updateTotalHeight(univ.getTotalHeight() + diff);
        univRepository.save(univ);
        // 멤버의 눈사람 키 갱신
        member.updateSnowmanHeight(member.getSnowmanHeight() + diff);
        return memberRepository.save(member);
    }

    // 공격하기. 눈송이 사용에 실패한 경우 MemberException
    public Member useSnowflakeForAttack() throws MemberException {
        Member member = authUtils.getMember();
        member.useSnowflake();
        return memberRepository.save(member);
    }
}
