package com.Kidari.server.domain.member.dto;

import com.Kidari.server.domain.member.entity.Member;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoResDto { // 유저 정보 페이지 조회
    private String nickname;
    private Long snowmanHeight;
    private Long snowId;
    private Long hatId;
    private Long decoId;
    private boolean isFollowed;

    public MemberInfoResDto(Member member, Boolean followStatus) {
        this.nickname = member.getLogin();
        this.snowmanHeight = member.getSnowmanHeight();
        this.snowId = member.getItem().getSnowId();
        this.hatId = member.getItem().getHatId();
        this.decoId = member.getItem().getDecoId();
        this.isFollowed = followStatus;
    }
}