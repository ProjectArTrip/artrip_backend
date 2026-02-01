package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.ReissueRequest;
import org.atdev.artrip.controller.spec.AuthSpecification;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AuthService;
import org.atdev.artrip.controller.dto.request.SocialLoginRequest;
import org.atdev.artrip.controller.dto.response.SocialLoginResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthSpecification {

    private final AuthService authService;


    @Override
    @PostMapping("/web/reissue")
    public ResponseEntity<String> webReissue(
            @CookieValue(value = "refreshToken", required = false) ReissueRequest refreshToken,
            HttpServletResponse response) {

        String newAccessToken = authService.webReissueToken(refreshToken, response);

        return ResponseEntity.ok(newAccessToken);
    }


    @PostMapping("/app/reissue")
    public ResponseEntity<SocialLoginResponse> appReissue(@RequestBody (required = false) ReissueRequest refreshToken) {

        SocialLoginResponse jwt = authService.appReissueToken(refreshToken);

        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/web/logout")
    public ResponseEntity<Void> webLogout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                       HttpServletResponse response) {

        authService.webLogout(refreshToken, response);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/app/logout")
    public void appLogout(@RequestBody(required = false) ReissueRequest token) {

        authService.appLogout(token);
    }


    @PostMapping("/social")
    public ResponseEntity<SocialLoginResponse> socialLogin(@RequestBody SocialLoginRequest request) {

        SocialLoginResponse jwt = authService.loginWithSocial(request.getProvider(), request.getIdToken());

        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> completeOnboarding(
            @LoginUser Long userId) {

        authService.completeOnboarding(userId);

        return ResponseEntity.noContent().build();
    }


}
