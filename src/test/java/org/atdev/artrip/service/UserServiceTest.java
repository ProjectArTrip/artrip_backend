package org.atdev.artrip.service;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void FcmTokenInfos() {
        SocialUserInfo socialUserInfo = new SocialUserInfo(
                "test@test.com",
                "testUser",
                "123444",
                Provider.KAKAO
        );

        testUser = User.of(socialUserInfo);
    }


//
//    @Test
//    @DisplayName("FCM 토큰 업데이트")
//    //given
//    Long userId = 1L;
//    String newToken = "token1234";
//    User user = User.builder().userId(userId).fcmToken("old_token").build();
//
//    given
}
