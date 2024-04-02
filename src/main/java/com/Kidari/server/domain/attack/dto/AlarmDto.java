package com.Kidari.server.domain.attack.dto;

import lombok.Builder;

@Builder
public class AlarmDto {
    private String time;
    private String attacker;
    private Boolean isChecked;
}
