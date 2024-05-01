package com.Kidari.server.domain.attack.service;

import com.Kidari.server.common.response.ApiResponse;
import com.Kidari.server.common.response.exception.MemberException;
import com.Kidari.server.common.validation.ValidationService;
import com.Kidari.server.domain.attack.entity.Attack;
import com.Kidari.server.domain.attack.entity.AttackRepository;
import com.Kidari.server.domain.attack.dto.AlarmDto;
import com.Kidari.server.domain.attack.dto.AlarmListResDto;
import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmCheckService {
    private final MemberService memberService;
    private final AttackRepository attackRepository;
    private final ValidationService validationService;

    // 최신 알림 읽기 (없을 수 있으며 최대 50개)
    public ApiResponse<?> readAlarms() {
        Member member = memberService.checkNewAlarm();
        List<Attack> attackedList = attackRepository.findTop50ByMemberOrderByTimeDesc(member);
        if (attackedList == null || attackedList.isEmpty()) //공격받은 기록이 없는 경우
            return ApiResponse.success();
        return ApiResponse.success(getAlarmListDto(attackedList)); // null일 수 있다
    }

    // Attack 받은 리스트를 AlarmListDto로 변환
    private AlarmListResDto getAlarmListDto(List<Attack> attackedList) {
        List<AlarmDto> alarmDtoList = attackedList.stream()
                .map(attack -> new AlarmDto(
                        attack.getTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")),
                        getAttackerLogin(attack),
                        attack.getIsChecked()))
                .collect(Collectors.toList());
        // attackedList의 isChecked를 모두 true로 설정
        attackedList.forEach(attack -> attack.checked());
        attackRepository.saveAll(attackedList);
        return new AlarmListResDto(alarmDtoList);
    }

    // Attacker가 탈퇴한 사용자인 경우 withdrawn_user로 출력
    private String getAttackerLogin(Attack attack) {
        try {
            return validationService.valMember(attack.getAttackerUid()).getLogin();
        } catch (MemberException e) {
            return "withdrawn_user"; // 탈퇴한 사용자인 경우
        }
    }
}
