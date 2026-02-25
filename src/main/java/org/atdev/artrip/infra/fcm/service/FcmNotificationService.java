package org.atdev.artrip.infra.fcm.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.FcmErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.fcm.service.dto.NotificationCommand;
import org.atdev.artrip.infra.fcm.service.dto.NotificationSingleCommand;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)

    public void test(Long userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        if (user.getFcmToken() == null || user.getFcmToken().isBlank()) {
            throw new GeneralException(FcmErrorCode._INVALID_REQUEST_MESSAGE);
        }

        NotificationSingleCommand command = new NotificationSingleCommand(
                user.getFcmToken(),
                "title = 푸시 알림 테스트",
                "body = 푸시 알림 테스트 입니다."
        );

        sendMessage(command);
    }

    public void sendMessage(final NotificationSingleCommand command) {
        try {

            Message message = command.builderMessage()
                            .setApnsConfig(getApnsConfig(command))
                            .build();

            firebaseMessaging.sendAsync(message);

        } catch (RuntimeException e) {
            throw new GeneralException(FcmErrorCode._FCM_SERVICE_UNAVAILABLE);
        }
    }

    private ApnsConfig getApnsConfig(NotificationCommand command) {

        ApsAlert alert = ApsAlert.builder()
                .setTitle(command.title())
                .setBody(command.body()).build();

        Aps aps = Aps.builder()
                .setAlert(alert)
                .setSound("default").build();

        return ApnsConfig.builder().setAps(aps).build();
    }
}
