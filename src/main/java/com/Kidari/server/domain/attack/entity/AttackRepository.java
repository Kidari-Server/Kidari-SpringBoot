package com.Kidari.server.domain.attack.entity;

import com.Kidari.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttackRepository extends JpaRepository<Attack, Long> {
    List<Attack> findTop50ByMemberOrderByTimeDesc(Member member);
}
