package org.atdev.artrip.service;

import org.atdev.artrip.constants.ExhibitReportStatus;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.exhibitReport.ExhibitReport;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportCreatedEvent;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.ExhibitReportRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.ExhibitReportCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExhibitReportServiceTest {

    @Mock
    ExhibitReportRepository exhibitReportRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    ExhibitReportService exhibitReportService;

    private User reporter;

    @BeforeEach
    void setUp() {
        reporter = User.builder().userId(1L).role(Role.USER).build();
    }

    @Test
    @DisplayName("전시 추가 요청 시 pending 상태로 저장되고 접수 완료 이벤트 발행")
    void saveExhibition_pendingState_emitEvent() {
        //given
        ExhibitReportCommand command = new ExhibitReportCommand("모네 특별전", "일본");
        when(userRepository.findById(reporter.getUserId())).thenReturn(Optional.of(reporter));

        //when
        exhibitReportService.create(reporter.getUserId(), command);

        //then
        ArgumentCaptor<ExhibitReport> savedCaptor = ArgumentCaptor.forClass(ExhibitReport.class);

        verify(exhibitReportRepository).save(savedCaptor.capture());
        ExhibitReport saved = savedCaptor.getValue();
        assertAll(
                () -> assertThat(saved.getStatus()).isEqualTo(ExhibitReportStatus.PENDING),
                () -> assertThat(saved.getTitle()).isEqualTo(command.title()),
                () -> assertThat(saved.getCountry()).isEqualTo(command.country()),
                () -> assertThat(saved.getUser()).isEqualTo(reporter)
        );

        ArgumentCaptor<ExhibitReportCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ExhibitReportCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().title()).isEqualTo(command.title());
    }

    @Test
    @DisplayName("존재하지 않는 유저가 신청 할 경우 user_not_found 처리")
    void addExhibition_ThrowsUserNotFound() {
        //given
        ExhibitReportCommand command = new ExhibitReportCommand("전시", "일본");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> exhibitReportService.create(999L, command))
                .isInstanceOfSatisfying(GeneralException.class, ex -> assertThat(ex.getCode()).isEqualTo(UserErrorCode._USER_NOT_FOUND));

        verifyNoInteractions(exhibitReportRepository, eventPublisher);
    }


}



