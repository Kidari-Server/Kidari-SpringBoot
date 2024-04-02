package com.Kidari.server.domain.attack.service;

import com.Kidari.server.common.response.ApiResponse;
import com.Kidari.server.common.response.exception.MemberException;
import com.Kidari.server.common.validation.ValidationService;
import com.Kidari.server.config.auth.AuthUtils;
import com.Kidari.server.domain.attack.entity.Attack;
import com.Kidari.server.domain.attack.entity.AttackRepository;
import com.Kidari.server.domain.attack.dto.AlarmDto;
import com.Kidari.server.domain.attack.dto.AlarmListDto;
import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.entity.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AlarmCheckService {
    private final AuthUtils authUtils;
    private final AttackRepository attackRepository;
    private final MemberRepository memberRepository;
    private final ValidationService validationService;

    // 알람을 최신순으로 정렬하여 체크한후 응답하는 메서드
    public ApiResponse<?> readAlarms() {
        Member member = authUtils.getMember();
        member.alarmChecked();  // 내 알림을 확인된 상태로 변경
        memberRepository.save(member);
        List<Attack> attackList = getSortedAttacks(member);
        if (attackList == null)
            return ApiResponse.success(); // 내용 없음
        return ApiResponse.success(convertAndMarkAttacksAsChecked(attackList));
    }

    // 받은 Attack 리스트를 최신순으로 정렬해 반환하는 메서드
    private List<Attack> getSortedAttacks(Member member) {
        List<Attack> attackedList = attackRepository.findAllByMember(member); // 내가 공격받은 목록 가져오기
        if (attackedList == null || attackedList.isEmpty()) //공격받은 기록이 없는 경우
            return null;
        attackedList.sort(Comparator.comparing(Attack::getTime).reversed()); // 최근에 공격된 것부터
        return attackedList;
    }

    // Attack 리스트를 AlarmListDto로 변환하는 메서드
    private AlarmListDto convertAndMarkAttacksAsChecked(List<Attack> attackedList){
        List<AlarmDto> alarmDtoList = new ArrayList<>();
        for (Attack attack : attackedList) {
            AlarmDto alarmDto = AlarmDto.builder()
                    .time(attack.getTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                    .attacker(getAttackerLogin(attack))
                    .isChecked(attack.getIsChecked())
                    .build();
            alarmDtoList.add(alarmDto);
            attack.checked();
        }
        attackRepository.saveAll(attackedList);
        return new AlarmListDto(alarmDtoList);
    }

    // 탈퇴한 사용자여서 Attacker 이름을 찾을 수 없는 경우를 위한 메서드
    private String getAttackerLogin(Attack attack) {
        try {
            return validationService.valMember(attack.getAttackerUid()).getLogin();
        } catch (MemberException e) {
            return "withdrawn_user"; // 탈퇴한 사용자인 경우
        }
    }
}
