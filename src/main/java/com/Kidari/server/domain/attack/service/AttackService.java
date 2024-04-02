package com.Kidari.server.domain.attack.service;

import com.Kidari.server.common.response.ApiResponse;
import com.Kidari.server.common.response.exception.MemberException;
import com.Kidari.server.common.validation.ValidationService;
import com.Kidari.server.config.auth.AuthUtils;
import com.Kidari.server.domain.attack.entity.Attack;
import com.Kidari.server.domain.attack.entity.AttackRepository;
import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.entity.MemberRepository;
import com.Kidari.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttackService {
    private final AuthUtils authUtils;
    private final AttackRepository attackRepository;
    private final MemberRepository memberRepository;
    private final ValidationService validationService;
    private final MemberService memberService;

    @Transactional
    public ApiResponse<?> doAttack(String attackedLogin){ // 공격을 시도함
        Member attacker = authUtils.getMember();
        try {
            attacker.useSnowflake(); // Exception 발생 가능
            Member attacked = validationService.valMember(attackedLogin); // Exception 발생 가능
            memberRepository.save(attacker); // 변경된 눈송이 갯수 저장
            createAttack(attacker, attacked);
            return ApiResponse.success(); // 응답 특별히 x
        } catch (MemberException e) {
            return ApiResponse.failure(e.getErrorCode()); // 눈송이 or 멤버 못 찾음
        }
    }

    private void createAttack(Member attacker, Member attacked){
        LocalDateTime time = LocalDateTime.now(); // 현재 시각
        attacked.alarmUnchecked(); // 공격받은 사람의 newAlarm 을 true 로
        Attack newAttack = Attack.builder()
                .time(time)
                .attackerUid(attacker.getUid())
                .isChecked(false)
                .member(attacked)
                .build();
        memberService.refreshHeight(attacked, -1L, attacked.getSnowmanHeight() - 1); // 공격받은 사람의 눈사람 키를 줄임
        attackRepository.save(newAttack);
    }
}
