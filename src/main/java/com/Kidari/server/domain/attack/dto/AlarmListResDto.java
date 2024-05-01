package com.Kidari.server.domain.attack.dto;

import java.util.List;

public record AlarmListResDto(List<AlarmDto> attackedList){}

/*
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmListDto {
    private List<AlarmDto> attackedList;
}
 */