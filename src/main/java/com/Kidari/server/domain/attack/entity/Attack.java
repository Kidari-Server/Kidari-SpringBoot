package com.Kidari.server.domain.attack.entity;

import com.Kidari.server.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Attack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attackId")
    private Long id; // 공격 고유 번호

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul") // 우선 서울 기준으로
    private LocalDateTime time; // 공격 시간

    @Column(nullable = false)
    private Long attackerId; // 공격자 고유 아이디

    @Column(nullable = false)
    private Boolean isChecked; // 단일 공격값 조회 여부

    // 단방향 매핑
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "memberId")
    private Member member; // 공격 받은 사람 고유 번호

    public void checkAttack(){
        this.isChecked = true;
    }
}
