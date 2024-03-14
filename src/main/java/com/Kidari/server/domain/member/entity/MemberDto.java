package com.Kidari.server.domain.member.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    String sub; // 깃허브에서 받은 고유 식별값
    String login; // 깃허브 아이디
    Role role; // 멤버 역할
}
