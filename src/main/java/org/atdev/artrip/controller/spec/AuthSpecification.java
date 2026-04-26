package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import org.atdev.artrip.controller.dto.request.LogoutRequest;
import org.atdev.artrip.controller.dto.request.ReissueRequest;
import org.atdev.artrip.controller.dto.request.SocialLoginRequest;
import org.atdev.artrip.controller.dto.response.AppReissueResponse;
import org.atdev.artrip.controller.dto.response.SocialLoginResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AuthSpecification {

    @PermitAll
    @Operation(summary = "토큰 재발행 (웹 전용)", description = "refresh토큰으로 access토큰을 재발행합니다")
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._INVALID_REFRESH_TOKEN, UserErrorCode._INVALID_USER_REFRESH_TOKEN},
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED, CommonErrorCode._INTERNAL_SERVER_ERROR}
    )
    public ResponseEntity<String> webReissue(
            @CookieValue(value = "refreshToken", required = false) ReissueRequest refreshToken,
            HttpServletResponse response);

    @PermitAll
    @Operation(summary = "토큰 재발행 (앱 전용)", description = "refresh토큰으로 access토큰을 재발행합니다")
    @ApiErrorResponses(
            user = {
                    UserErrorCode._USER_NOT_FOUND,
                    UserErrorCode._INVALID_REFRESH_TOKEN,
                    UserErrorCode._INVALID_USER_REFRESH_TOKEN,
                    UserErrorCode._JWT_EXPIRED_REFRESH_TOKEN,
            },
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED, CommonErrorCode._INTERNAL_SERVER_ERROR}
    )
    public ResponseEntity<AppReissueResponse> appReissue(@RequestBody(required = false) ReissueRequest refreshToken);


    @PermitAll
    @Operation(summary = "로그아웃 (웹 전용)", description = "refresh, access 토큰을 제거합니다.")
    @ApiErrorResponses(
            user = {UserErrorCode._INVALID_REFRESH_TOKEN},
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED, CommonErrorCode._INTERNAL_SERVER_ERROR}
    )
    public ResponseEntity<Void> webLogout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                          HttpServletResponse response);

    @Operation(summary = "로그아웃 (앱 전용)", description = "refresh, access 토큰을 제거합니다.")
    @ApiErrorResponses(
            user = {UserErrorCode._INVALID_REFRESH_TOKEN, UserErrorCode._INVALID_USER_REFRESH_TOKEN},
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED, CommonErrorCode._INTERNAL_SERVER_ERROR}
    )
    public ResponseEntity<Void> appLogout(@LoginUser Long userId,
                                          @RequestBody LogoutRequest token,
                                          @RequestHeader("Authorization") String authorization);


    @PermitAll
    @Operation(summary = "소셜 SDK 토큰 검증 후 jwt 발급",
            description = "만료일 : refresh: 7일 , access: 15분 | onboardingStep: NICKNAME(닉네임 미설정) / KEYWORD(닉네임 설정 완료·키워드 미설정) / COMPLETED(온보딩 완료)")
    @ApiErrorResponses(
            user = {
                    UserErrorCode._SOCIAL_ID_TOKEN_INVALID,
                    UserErrorCode._USER_NOT_FOUND,
                    UserErrorCode._SOCIAL_VERIFICATION_FAILED,
                    UserErrorCode._SOCIAL_TOKEN_EXPIRED,
                    UserErrorCode._SOCIAL_TOKEN_INVALID_SIGNATURE,
                    UserErrorCode._SOCIAL_TOKEN_INVALID_AUDIENCE,
            },
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED, CommonErrorCode._INTERNAL_SERVER_ERROR}
    )
    public ResponseEntity<SocialLoginResponse> socialLogin(@RequestBody SocialLoginRequest request);

    @Operation(summary = "회원 탈퇴", description = "리뷰,즐찾,키워드 전부 Hard Delete, 소셜 unlink")
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._INVALID_REFRESH_TOKEN, UserErrorCode._INVALID_USER_REFRESH_TOKEN}
    )
    public ResponseEntity<Void> withdraw(@LoginUser Long userId,
                                         @RequestBody(required = false) LogoutRequest token,
                                         @RequestHeader("Authorization") String authorization);
}