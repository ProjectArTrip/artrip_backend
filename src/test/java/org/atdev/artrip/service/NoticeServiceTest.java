package org.atdev.artrip.service;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.error.NoticeErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.fcm.FcmNotificationService;
import org.atdev.artrip.repository.NoticeRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.AdminNoticeCreateCommand;
import org.atdev.artrip.service.dto.command.AdminNoticeUpdateCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {

    @InjectMocks
    private AdminNoticeService adminNoticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FcmNotificationService fcmNotificationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        SocialUserInfo socialUserInfo = new SocialUserInfo(
                "test@test.com",
                "testUser",
                "1234554",
                Provider.KAKAO
        );
        testUser = User.createUser(socialUserInfo);
    }

    @Test
    @DisplayName("공지사항 생성 - Role Admin")
    void createNoticeUserIsNotAdmin() {
        //given
        AdminNoticeCreateCommand command = new AdminNoticeCreateCommand(1L, "공지사항 생성생성생성", "내용입니다아아아아아");
        User user = User.builder()
                .userId(1L)
                .name("testUser")
                .role(Role.USER)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        //when
        //then
        assertThatThrownBy(() -> adminNoticeService.createNotice(command))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(UserErrorCode._USER_FORBIDDEN);
    }

    @Test
    @DisplayName("관리자 공지사항 수정 - 존재하지 않는 공지")
    void updateNoticeNotFound() {
        //given
        AdminNoticeUpdateCommand command = new AdminNoticeUpdateCommand(1L, 0L,"title",  "수정된 공지사항");
        User admin = User.builder()
                .email("test@test.com")
                .name("testUser")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(admin));
        when(noticeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> adminNoticeService.updateNotice(command))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(NoticeErrorCode._NOTICE_NOT_FOUND);
    }

    @Test
    @DisplayName("공지사항 삭제 - 존재하지 않는 공지")
    void deleteNoticeNotFound() {
        //given
        User admin = User.builder()
                .userId(1L)
                .role(Role.ADMIN)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(admin));
        when(noticeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> adminNoticeService.deleteNotice(1L, 10L))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(NoticeErrorCode._NOTICE_NOT_FOUND);
        verify(noticeRepository, never()).delete(any());
    }

}
