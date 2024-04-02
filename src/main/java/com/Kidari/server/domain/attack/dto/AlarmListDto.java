package com.Kidari.server.domain.attack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlarmListDto {
    private List<AlarmDto> alarmDtoList;
}
