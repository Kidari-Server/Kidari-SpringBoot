package com.Kidari.server.domain.follow.entity;

import com.Kidari.server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "followId")
    private Long id; // 친구 추가 고유 번호
    
    @Column(nullable = false)
    private UUID buddyUid; // 추가 당하는 사람 // Long에서 변경

    // 단방향 매핑
    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member; // 멤버 고유 번호

}
