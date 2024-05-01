package com.Kidari.server.domain.member.dto;

import com.Kidari.server.domain.member.entity.Member;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSearchResDto {
    private String nickname;
    private Long snowmanHeight;
    private boolean isFollowed;

    @Builder
    public MemberSearchResDto(Member member, boolean isFollowed) {
        this.nickname = member.getLogin();
        this.snowmanHeight = member.getSnowmanHeight();
        this.isFollowed = isFollowed;
    }
}
