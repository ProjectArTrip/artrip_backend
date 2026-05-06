package org.atdev.artrip.service;

import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.FcmErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("FCM 토큰 저장 - 존재하지 않는 유저")
    void fcmTokenSaveUserNotFound() {
        //given
        Long userId = 1L;
        String newToken = "token1234";

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> userService.updateFcmToken(userId, newToken))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(UserErrorCode._USER_NOT_FOUND);
    }

    @Test
    @DisplayName("FCM 토큰 저장 실패 - 잘못된 토큰 형식")
    void fcmTokenSaveInvalidToken() {
        //given
        Long userId = 1L;
        String token = "    ";

        User user = User.builder().userId(userId).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        //when
        //then
        assertThatThrownBy(() -> userService.updateFcmToken(userId, token))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(FcmErrorCode._INVALID_REQUEST_PATTERN);
    }

    @Test
    @DisplayName("FCM 토큰 제거 및 이미 null 일경우 예외 없이 종료")
    void handle_clear_fcm_token_null_without_exception() {
        //given
        Long userId = 1L;
        User user = User.builder()
                .userId(userId)
                .name("user")
                .role(Role.USER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        //then
        assertAll(
                () -> assertThatNoException().isThrownBy(() -> userService.clearFcmToken(userId)),
                () -> assertThat(user.getFcmToken()).isNull()
        );
    }
}
