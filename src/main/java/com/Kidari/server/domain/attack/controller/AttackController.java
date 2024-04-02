package com.Kidari.server.domain.attack.controller;

import com.Kidari.server.common.response.ApiResponse;
import com.Kidari.server.common.response.exception.MemberException;
import com.Kidari.server.domain.attack.service.AttackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AttackController {

    private final AttackService attackService;

    @PatchMapping("/user/attack")
    public ApiResponse<?> attackUser(@RequestParam String nickname) throws MemberException {
        return attackService.doAttack(nickname);
    }
}
