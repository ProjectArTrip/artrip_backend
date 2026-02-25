package org.atdev.artrip.infra.fcm.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.infra.fcm.service.FcmNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/test")
public class TestApiController {

    private final FcmNotificationService fcmNotificationService;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public String test() {
        return "server connect";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/alarm")
    public ResponseEntity<?> pushAlarm(@LoginUser Long userId) {
        fcmNotificationService.test(userId);
        return ResponseEntity.ok("성공");
    }
}
