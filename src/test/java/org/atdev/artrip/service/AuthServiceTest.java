package org.atdev.artrip.service;

import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.jwt.JwtGenerator;
import org.atdev.artrip.jwt.JwtProvider;
import org.atdev.artrip.repository.SocialRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.redis.RedisService;
import org.atdev.artrip.validator.social.SocialVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;
    @Mock
    private RedisService redisService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private JwtGenerator jwtGenerator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SocialRepository socialRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private List<SocialVerifier> socialVerifiers;

    @Test
    @DisplayName("로그아웃 후 FCM 제거 후 세션 정리")
    void cleanUp_sessions_after_logging_out_and_removing_FCM() {
        //given
        Long userId = 1L;
        String accessToken = "ACCESS";
        String refreshToken = "REFRESH";
        given(redisService.getValue(refreshToken)).willReturn(String.valueOf(userId));
        given(jwtProvider.getExpiration(accessToken)).willReturn(1_000L);

        //when
        authService.appLogout(userId, accessToken, refreshToken);

        //then
        verify(userService).clearFcmToken(userId);
        verify(redisService).deleteKey(refreshToken);
    }

    @Test
    @DisplayName("로그아웃 시 FCM 제거 실패시 세션 보존")
    void session_preservation_if_FCM_removal_fails_upon_logout() {
        // given
        Long userId = 1L;
        String accessToken = "ACCESS";
        String refreshToken = "REFRESH";

        given(redisService.getValue(refreshToken)).willReturn(String.valueOf(userId));

        willThrow(new GeneralException(UserErrorCode._USER_NOT_FOUND)).given(userService).clearFcmToken(userId);

        // when
        // then
        assertThatThrownBy(() -> authService.appLogout(userId, accessToken, refreshToken)).isInstanceOf(GeneralException.class);

        verify(redisService, never()).save(anyString(), anyString(), anyLong());
        verify(redisService, never()).deleteKey(refreshToken);
    }
}
