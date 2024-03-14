package com.Kidari.server.domain.commit.entity;

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
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commitId")
    private Long id; // 커밋 고유 번호

    @Column(nullable = false)
    private Integer count; // 커밋 수

    // 단방향 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    public void updateCount(Integer totalCommits) {
        this.count = totalCommits;
    }
}

