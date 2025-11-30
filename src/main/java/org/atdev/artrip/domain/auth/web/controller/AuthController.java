package org.atdev.artrip.domain.auth.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.jwt.JwtToken;
import org.atdev.artrip.domain.auth.jwt.repository.RefreshTokenRedisRepository;
import org.atdev.artrip.domain.auth.service.AuthService;
import org.atdev.artrip.domain.auth.web.dto.ReissueRequest;
import org.atdev.artrip.domain.auth.web.dto.SocialLoginRequest;
import org.atdev.artrip.domain.auth.web.dto.SocialLoginResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "토큰 재발행 (웹 전용)", description = "refresh토큰으로 access토큰을 재발행합니다")
    @ApiErrorResponses(
            user = {UserError._USER_NOT_FOUND, UserError._INVALID_REFRESH_TOKEN},
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED, CommonError._INTERNAL_SERVER_ERROR}
    )
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<String>> reissue(
            @CookieValue(value = "refreshToken", required = false) ReissueRequest refreshToken,
            HttpServletResponse response) {

        String newAccessToken = authService.reissueToken(refreshToken, response);

        return ResponseEntity.ok(CommonResponse.onSuccess(newAccessToken));
    }

    @Operation(summary = "토큰 재발행 (앱 전용)", description = "refresh토큰으로 access토큰을 재발행합니다")
    @ApiErrorResponses(
            user = {UserError._USER_NOT_FOUND, UserError._INVALID_REFRESH_TOKEN, UserError._INVALID_USER_REFRESH_TOKEN},
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED, CommonError._INTERNAL_SERVER_ERROR}
    )
    @PostMapping("/app/reissue")
    public ResponseEntity<CommonResponse<SocialLoginResponse>> appReissue(
            @RequestBody (required = false) ReissueRequest refreshToken) {

        SocialLoginResponse jwt = authService.reissueAppToken(refreshToken);

        return ResponseEntity.ok(CommonResponse.onSuccess(jwt));
    }

    @Operation(summary = "로그아웃", description = "refresh, access 토큰을 제거합니다.")
    @ApiErrorResponses(
            user = {UserError._INVALID_REFRESH_TOKEN},
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED, CommonError._INTERNAL_SERVER_ERROR}
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                       HttpServletResponse response) {

        authService.logout(refreshToken, response);

        return ResponseEntity.ok("로그아웃 완료");
    }

    @Operation(summary = "소셜 SDK 토큰 검증 후 jwt 발급", description = "만료일 : refresh: 7일 , access: 15분 ,isFirstLogin true:회원가입 false:로그인")
    @ApiErrorResponses(
            user = {UserError._SOCIAL_ID_TOKEN_INVALID, UserError._USER_NOT_FOUND},
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED, CommonError._INTERNAL_SERVER_ERROR}
    )
    @PostMapping("/social")
    public ResponseEntity<CommonResponse<SocialLoginResponse>> socialLogin(@RequestBody SocialLoginRequest request) {

        SocialLoginResponse jwt = authService.loginWithSocial(request.getProvider(), request.getIdToken());

        return ResponseEntity.ok(CommonResponse.onSuccess(jwt));
    }

}
