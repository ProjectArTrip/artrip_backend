package org.atdev.artrip.infra.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.infra.notification.NotificationReference;
import org.atdev.artrip.infra.notification.PushProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FcmNotificationServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final FcmMessageSender messageSender = mock(FcmMessageSender.class);
    private final FcmNotificationService service = new FcmNotificationService(
            userRepository, messageSender, mock(PlatformTransactionManager.class));

    private final NotificationMessage message = NotificationMessage.of(NotificationReference.notice(1L), "제목", "본문");

    @BeforeEach
    void setUp() throws FirebaseMessagingException {
        BatchResponse ok = mock(BatchResponse.class);
        when(ok.getFailureCount()).thenReturn(0);
        when(messageSender.sendMulticast(any())).thenReturn(ok);
    }

    @Test
    @DisplayName("브로드캐스트는 500명씩 나눠 보내고, 청크마다 커서를 전진시킨다")
    void broadcastInChunks() throws Exception {
        when(userRepository.findValidPushUsersAfter(eq(0L), any(Pageable.class))).thenReturn(users(1, 500));
        when(userRepository.findValidPushUsersAfter(eq(500L), any(Pageable.class))).thenReturn(users(501, 503));
        RecordingProgress progress = new RecordingProgress(null);

        service.sendBroadcast(message, progress);

        ArgumentCaptor<NotificationMulticastDispatch> captor = ArgumentCaptor.forClass(NotificationMulticastDispatch.class);
        verify(messageSender, times(2)).sendMulticast(captor.capture());
        assertThat(captor.getAllValues()).extracting(d -> d.targetTokens().size()).containsExactly(500, 3);
        assertThat(progress.advanced).containsExactly(500L, 503L);
    }

    @Test
    @DisplayName("커서가 있으면 그 다음 사용자부터 보낸다")
    void resumeFromCursor() throws Exception {
        when(userRepository.findValidPushUsersAfter(eq(500L), any(Pageable.class))).thenReturn(users(501, 502));

        service.sendBroadcast(message, new RecordingProgress(500L));

        verify(userRepository, never()).findValidPushUsersAfter(eq(0L), any(Pageable.class));
        verify(messageSender, times(1)).sendMulticast(any());
    }

    @Test
    @DisplayName("멀티캐스트에서 등록 해제된 토큰은 무효화한다")
    void invalidateUnregisteredToken() throws Exception {
        SendResponse success = mock(SendResponse.class);
        when(success.isSuccessful()).thenReturn(true);
        FirebaseMessagingException unregistered = mock(FirebaseMessagingException.class);
        when(unregistered.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);
        SendResponse failure = mock(SendResponse.class);
        when(failure.isSuccessful()).thenReturn(false);
        when(failure.getException()).thenReturn(unregistered);

        BatchResponse response = mock(BatchResponse.class);
        when(response.getFailureCount()).thenReturn(1);
        when(response.getResponses()).thenReturn(List.of(success, failure));
        when(messageSender.sendMulticast(any())).thenReturn(response);

        service.sendToUsers(users(1, 2), message);

        verify(userRepository).findByFcmToken("token-2");
        verify(userRepository, never()).findByFcmToken("token-1");
    }

    @Test
    @DisplayName("단건 발송에서 무효 토큰 오류는 예외 없이 토큰만 정리하고, 일시 오류는 다시 던진다")
    void singleSendErrors() throws Exception {
        User user = user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FirebaseMessagingException unregistered = mock(FirebaseMessagingException.class);
        when(unregistered.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);
        when(messageSender.sendSingle(any())).thenThrow(unregistered);
        service.sendToUser(1L, message);
        verify(userRepository).findByFcmToken("token-1");

        FirebaseMessagingException unavailable = mock(FirebaseMessagingException.class);
        when(unavailable.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNAVAILABLE);
        reset(messageSender);
        when(messageSender.sendSingle(any())).thenThrow(unavailable);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.sendToUser(1L, message))
                .isSameAs(unavailable);
    }

    private static List<User> users(long fromId, long toId) {
        return LongStream.rangeClosed(fromId, toId).mapToObj(FcmNotificationServiceTest::user).toList();
    }

    private static User user(long id) {
        User user = User.builder().userId(id).name("user" + id).build();
        user.updateFcmToken("token-" + id);
        return user;
    }

    private static class RecordingProgress implements PushProgress {
        private Long cursor;
        private final List<Long> advanced = new ArrayList<>();

        RecordingProgress(Long cursor) {
            this.cursor = cursor;
        }

        @Override
        public Long cursor() {
            return cursor;
        }

        @Override
        public void advance(Long lastUserId) {
            cursor = lastUserId;
            advanced.add(lastUserId);
        }
    }
}
