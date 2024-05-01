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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        // TODO: updateSnowflake 갱신 메소드 호출 추후 추가 필요
        Member member = authUtils.getMember();
        return ApiResponse.success(new MemberHomeResDto(member));
    }

    // 유저를 nickname으로 검색, 내가 follow중인 상태인지와 함께 보냄
    public ApiResponse<?> searchMember(String nickname) {
        Member member = authUtils.getMember();
        List<Member> searchedMembers = memberRepository.findByLoginStartingWith(nickname);
        if (searchedMembers == null)
            return ApiResponse.success(HttpStatus.NO_CONTENT); // 검색 결과 없음
        List<MemberSearchResDto> resDtos = searchedMembers.stream()
                .map(otherMember -> new MemberSearchResDto(otherMember, followService.followStatus(member, otherMember.getUid())))
                .toList();
        return ApiResponse.success(resDtos);
    }

    // 다른 단일 유저 정보 조회
    public ApiResponse<?> getMemberInfo(String nickname){
        Member otherMember = validationService.valMember(nickname);
        Member member = authUtils.getMember();
        Boolean followStatus = followService.followStatus(member, otherMember.getUid());
        return ApiResponse.success(new MemberInfoResDto(otherMember, followStatus)); // 팔로우 상태와 함께 보냄
    }

    // 내 눈사람 키 키우기. 눈송이 사용에 실패한 경우 MemberException
    public ApiResponse<?> growSnowman() {
        try {
            Member member = authUtils.getMember();
            // member.updateSnowflake(); // TODO: 추후 변경 필요
            member.useSnowflake();
            refreshHeight(member, 1L);
            return ApiResponse.success();
        } catch (MemberException e) {
            return ApiResponse.failure(e.getErrorCode());
        }
    }

    // 내 알림을 확인된 상태로 변경
    public Member checkNewAlarm() {
        Member member = authUtils.getMember();
        member.alarmChecked();
        return memberRepository.save(member);
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
        // member.updateSnowflake(1L); // TODO: 추후 변경 필요
        member.useSnowflake();
        return memberRepository.save(member);
    }
}
