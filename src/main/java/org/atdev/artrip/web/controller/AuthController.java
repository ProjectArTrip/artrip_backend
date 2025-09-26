package org.atdev.artrip.web.controller;

import com.nimbusds.openid.connect.sdk.LogoutRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.auth.jwt.JwtGenerator;
import org.atdev.artrip.auth.jwt.JwtProvider;
import org.atdev.artrip.auth.jwt.entity.RefreshToken;
import org.atdev.artrip.auth.jwt.exception.JwtAuthenticationException;
import org.atdev.artrip.auth.jwt.repository.RefreshTokenRepository;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private RefreshTokenRepository refreshTokenRepository;
    private JwtGenerator jwtGenerator;
    private JwtProvider jwtProvider;

//    @PostMapping("/reissue")
//    public ResponseEntity<ApiResponse<String>> reissue(
//            @CookieValue(value = "refreshToken", required = false) String refreshToken,
//            HttpServletResponse response) {
//
//        if (refreshToken == null) {
//            throw new JwtAuthenticationException("리프레시 토큰이 존재하지 않습니다.");
//        }
//
//
//        // 1. 유효성 검증
//        jwtProvider.validateRefreshToken(refreshToken);
//
//
//        // 2. 저장된 리프레시 토큰과 비교
//        RefreshToken savedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
//                .orElseThrow(() -> new JwtAuthenticationException("DB에 저장된 리프레시 토큰이 아닙니다."));
//
//        // 3. 새로운 액세스 토큰 발급
//        String newAccessToken = jwtGenerator.createAccessToken(savedToken.getUsername(), "ROLE_USER");
//
//        // 4. 쿠키에 리프레시 토큰 다시 저장 (만료 시간 갱신 목적) 아래 설정을 하기 위해 쿠키에 넣는것임!
//        Cookie newRefreshCookie = new Cookie("refreshToken", refreshToken);// 쿠키 이름과 토큰값 설정
//        newRefreshCookie.setHttpOnly(true);//js에서 접근을 못하게 하는 설정, xss 공격 방지에 효과적(웹해킹공격)
//        newRefreshCookie.setSecure(false); // true로 바꾸면 HTTPS에서만 쿠키 전송이 가능해짐 SSL, HTTPS 베포 // http=암호화되지않은 연결, 개발환경에서는 false로 둠
//        newRefreshCookie.setPath("/");// 쿠키가 허용한 url경로
//        newRefreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 refreshtoken과 만료시점을 맞춤
//
//        response.addCookie(newRefreshCookie);
//
//
//        // 5. 액세스 토큰만 응답 바디로 전달
//        return ResponseEntity.ok(new ApiResponse<>(true, "200", "성공", newAccessToken));
//    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        return null;
    }
}
