package org.atdev.artrip.domain.auth.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.jwt.JwtToken;
import org.atdev.artrip.domain.auth.jwt.repository.RefreshTokenRedisRepository;
import org.atdev.artrip.domain.auth.service.AuthService;
import org.atdev.artrip.domain.auth.web.dto.SocialLoginRequest;
import org.atdev.artrip.domain.auth.web.dto.SocialLoginResponse;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final AuthService authService;


    @Operation(summary = "토큰 재발행", description = "refresh토큰으로 access토큰을 재발행합니다")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        String newAccessToken = authService.reissueToken(refreshToken, response);

        return ResponseEntity.ok(ApiResponse.onSuccess(newAccessToken));
    }

    @Operation(summary = "로그아웃", description = "refresh, access 토큰을 제거합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                       HttpServletResponse response) {

        authService.logout(refreshToken, response);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/social")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> socialLogin(@RequestBody SocialLoginRequest request) {

        JwtToken jwt = authService.loginWithSocial(request.getProvider(), request.getIdToken());

        SocialLoginResponse response = new SocialLoginResponse(
                jwt.getAccessToken(),
                jwt.getRefreshToken()
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

}
