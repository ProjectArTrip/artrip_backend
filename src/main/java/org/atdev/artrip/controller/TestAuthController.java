package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.TestLoginRequest;
import org.atdev.artrip.controller.dto.response.SocialLoginResponse;
import org.atdev.artrip.service.AuthService;
import org.atdev.artrip.service.dto.result.SocialLoginResult;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Profile("!prod")
@RequiredArgsConstructor
public class TestAuthController {

    private final AuthService authService;

    @PostMapping("/login/test")
    public ResponseEntity<SocialLoginResponse> testLogin(@RequestBody TestLoginRequest request) {

        SocialLoginResult result = authService.loginForAppleReview(request.email(), request.password());
        SocialLoginResponse response = SocialLoginResponse.from(result);

        return ResponseEntity.ok(response);
    }
}
