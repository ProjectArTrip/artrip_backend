package org.atdev.artrip.infra.fcm;

import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibitSync.ExhibitSyncedEvent;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationTemplate;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.UserNoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FcmExhibitSyncEventListenerTest {

    @Mock
    FcmNotificationService fcmNotificationService;

    @Mock
    UserNoticeService userNoticeService;

    @Mock
    UserRepository userRepository;

    FcmExhibitSyncEventListener listener;

    @Test
    @DisplayName("동기화 이벤트를 수신하면 push 대상 사용자를 조회해 알림을 저장하고 멀티캐스트로 발송한다")
    void onSynced_savesAndSendsNotificationToValidPushUsers() {
        listener = new FcmExhibitSyncEventListener(fcmNotificationService, userNoticeService, userRepository);
        User user = User.builder().userId(1L).name("tester").role(Role.USER).build();
        when(userRepository.findValidPushUsers()).thenReturn(List.of(user));

        listener.onSynced(new ExhibitSyncedEvent(7));

        ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(userNoticeService).saveAll(eq(List.of(user)), captor.capture());
        NotificationMessage message = captor.getValue();
        verify(fcmNotificationService).sendMulticastToUsers(eq(List.of(user)), eq(message));

        assertThat(message.title()).isEqualTo(NotificationTemplate.EXHIBIT_SYNC_WEEKLY.getTitle());
        assertThat(message.body()).isEqualTo("7건의 새로운 전시가 추가되었어요. 지금 확인해보세요.");
        assertThat(message.reference().toFcmData()).containsEntry("action", "MOVE_EXHIBIT_LIST");
        assertThat(message.reference().toFcmData()).doesNotContainKey("referenceId");
    }

    @Test
    @DisplayName("push 대상 사용자가 없어도 빈 리스트로 저장/발송 로직이 정상 호출된다")
    void onSynced_noPushUsers_callsWithEmptyList() {
        listener = new FcmExhibitSyncEventListener(fcmNotificationService, userNoticeService, userRepository);
        when(userRepository.findValidPushUsers()).thenReturn(List.of());

        listener.onSynced(new ExhibitSyncedEvent(3));

        verify(userNoticeService).saveAll(eq(List.of()), any());
        verify(fcmNotificationService).sendMulticastToUsers(eq(List.of()), any());
    }
}