package com.Kidari.server.domain.member.service;

import com.Kidari.server.common.response.exception.ErrorCode;
import com.Kidari.server.common.response.exception.MemberException;
import com.Kidari.server.config.auth.AuthUtils;
import com.Kidari.server.domain.member.dto.MemberHomeDto;
import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.entity.MemberRepository;
import com.Kidari.server.domain.univ.entity.Univ;
import com.Kidari.server.domain.univ.entity.UnivRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UnivRepository univRepository;
    private final AuthUtils authUtils;

    // 본인 정보 조회
    public MemberHomeDto getMemberInfo() {
        Member member = authUtils.getMember();
        // TODO: refreshSnowflake 메소드 작성 필요. 이 위치에서 member의 눈송이 수를 새로고침 해야 함
        return MemberHomeDto.builder()
                .nickname(member.getLogin()) // TODO: 응답 nickname으로 할지, login으로 할지 확인 필요
                .snowflake(member.getSnowflake())
                .snowmanHeight(member.getSnowmanHeight())
                .snowId(member.getItem().getSnowId())
                .hatId(member.getItem().getHatId())
                .decoId(member.getItem().getDecoId())
                .newAlarm(member.getNewAlarm())
                .build();
    }

    // 나의 눈사람 키 키우기
    public Member growSnowman() {
        Member member = authUtils.getMember();
        try {
            member.useSnowflake(); // 눈송이 사용에 실패한 경우 MemberException
            return refreshHeight(member, 1L, member.getSnowmanHeight() + 1);
        } catch (MemberException e) {
            return null;
        }
    }

    // 단일 멤버의 눈사람 키 갱신 (멤버와 Univ의 totalHeight), diff는 양수(키우기) 또는 음수(공격받음)
    public Member refreshHeight(Member member, Long diff, Long newHeight) {
        // Univ의 totalHeight 갱신
        Univ univ = member.getUniv();
        univ.updateTotalHeight(univ.getTotalHeight() + diff);
        univRepository.save(univ);
        // 멤버의 눈사람 키 갱신
        member.updateSnowmanHeight(member.getSnowmanHeight() + diff);
        return memberRepository.save(member);
    }

    // 유저 본인 정보 조회
    // 다른 유저 정보 조회
    // 유저를 nickname으로 검색, 내가 follow중인 상태인지와 함께 보냄

}
