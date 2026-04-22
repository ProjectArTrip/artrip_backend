package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.LogoutRequest;
import org.atdev.artrip.controller.dto.request.ReissueRequest;
import org.atdev.artrip.controller.dto.response.AppReissueResponse;
import org.atdev.artrip.controller.spec.AuthSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AuthService;
import org.atdev.artrip.controller.dto.request.SocialLoginRequest;
import org.atdev.artrip.controller.dto.response.SocialLoginResponse;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.service.dto.result.AppReissueResult;
import org.atdev.artrip.service.dto.result.SocialLoginResult;
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
    public ResponseEntity<AppReissueResponse> appReissue(@RequestBody ReissueRequest refreshToken) {

        AppReissueResult result = authService.appReissueToken(refreshToken.refreshToken());
        AppReissueResponse response = AppReissueResponse.from(result);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/web/logout")
    public ResponseEntity<Void> webLogout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                       HttpServletResponse response) {

        authService.webLogout(refreshToken, response);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/app/logout")
    public ResponseEntity<Void> appLogout(@LoginUser Long userId,
                                          @RequestBody LogoutRequest token,
                                          @RequestHeader("Authorization") String authorization) {

        authService.appLogout(userId, authorization.substring(7), token.refreshToken());

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/social")
    public ResponseEntity<SocialLoginResponse> socialLogin(@RequestBody SocialLoginRequest request) {

        SocialLoginResult result = authService.loginWithSocial(request.getProvider(), request.getIdToken(), request.getAuthorizationCode());
        SocialLoginResponse response = SocialLoginResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> completeOnboarding(
            @LoginUser Long userId) {

        authService.completeOnboarding(userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@LoginUser Long userId,
                                         @RequestBody LogoutRequest token,
                                         @RequestHeader("Authorization") String authorization) {

        authService.withdraw(userId, authorization.substring(7), token.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
