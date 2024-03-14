package com.Kidari.server.domain.univ.entity;

import jakarta.persistence.*;
import lombok.*;

import static java.lang.Math.max;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Univ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univId")
    private Long id; // 대학 고유 번호

    @Column(nullable = false, unique = true)
    private String univName; // 대학명

    @Column(nullable = false)
    private Long totalHeight; // 눈사람 키 총합

    @Column(nullable = false)
    private Long belonged; // 등록된 학생 수

    public void updateTotalHeight(Long diff) {
        this.totalHeight = max(this.totalHeight + diff, 0);
    }
}
