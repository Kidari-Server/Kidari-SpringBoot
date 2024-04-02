package com.Kidari.server.domain.attack.controller;

import com.Kidari.server.common.response.ApiResponse;
import com.Kidari.server.domain.attack.service.AlarmCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AlarmController {

    private final AlarmCheckService alarmCheckService;

    @GetMapping("/home/alarm") // 알림 조회 (공격받은 목록 조회)
    public ApiResponse<?> getAlarms() {
        return alarmCheckService.readAlarms();
    }
}