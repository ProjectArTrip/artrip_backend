package org.atdev.artrip.service;

import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.global.apipayload.code.status.NoticeStatusCode;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.NoticeRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.AdminNoticeCreateCommand;
import org.atdev.artrip.service.dto.result.AdminNoticeCreateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminExhibitServiceTest {

    @Mock
    private ExhibitRepository exhibitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private AdminNoticeService adminNoticeService;

//    @InjectMocks
//    private AdminExhibitService adminExhibitService;

    private User adminUser;
    private User normalInfo;
    private ExhibitHall testHall;
    private Exhibit ongoingExhibit;
    private Exhibit endingSoonExhibit;
    private Exhibit upcomingExhibit;

    //TODO: 관리자 전시, 리뷰 관련 테스트 코드 작성

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .userId(1L)
                .name("admin")
                .role(Role.ADMIN)
                .build();
    }


    @Test
    @DisplayName("공지사항 생성 - push 대상자 0명일 경우 예외가 아닌 no_push_target메세지 반환")
    public void createNoticeNoPushTarget() {
        //given
        AdminNoticeCreateCommand command = new AdminNoticeCreateCommand(1L, "공지 제목", "공지 내용");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(adminUser));
        when(userRepository.countValidPushUsers()).thenReturn(0L);

        //when
        AdminNoticeCreateResult result = adminNoticeService.createNotice(command);

        //then
        assertAll(
                () -> assertThat(result.pushUserCount()).isZero(),
                () -> assertThat(result.message()).isEqualTo(NoticeStatusCode.NOTICE_CREATE_NO_PUSH_TARGET.getMessage())
        );
    }

    @Test
    @DisplayName("공지사항 생성 - push 대상자 존재할 경우 WITH_PUSH메시지와 같이 인원수 반환")
    public void createNoticeWithPushTarget() {
        //given
        AdminNoticeCreateCommand command = new AdminNoticeCreateCommand(1L, "공지 제목", "공지 낸용");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(adminUser));
        when(userRepository.countValidPushUsers()).thenReturn(3L);

        //when
        AdminNoticeCreateResult result = adminNoticeService.createNotice(command);

        // then
        assertAll(
                () -> assertThat(result.pushUserCount()).isEqualTo(3L),
                () -> assertThat(result.message()).isEqualTo(String.format(NoticeStatusCode.NOTICE_CREATE_WITH_PUSH.getMessage(), 3L))
        );
    }
}
