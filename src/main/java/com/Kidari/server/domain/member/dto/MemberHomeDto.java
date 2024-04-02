package com.Kidari.server.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberHomeDto {
    String nickname; // 엔티티의 login
    Long snowflake;
    Long snowmanHeight;
    Long snowId;
    Long hatId;
    Long decoId;
    Boolean newAlarm;
}
