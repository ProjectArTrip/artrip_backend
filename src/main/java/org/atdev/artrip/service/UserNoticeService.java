package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.NotificationAction;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.notice.UserNotice;
import org.atdev.artrip.global.apipayload.code.status.NoticeErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.fcm.service.dto.NoticeCreatedEvent;
import org.atdev.artrip.repository.UserNoticeRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.result.UserNoticeCursorResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNoticeService {
    private final UserNoticeRepository userNoticeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserNoticeCursorResult getNotifications(Long userId, CursorPagination pagination) {
        User user = findUserOrThrow(userId);

        Slice<UserNotice> slice = userNoticeRepository.findAllByUser(user, pagination.cursor(), PageRequest.of(0, pagination.size().intValue()));

        return UserNoticeCursorResult.of(slice);
    }

    @Transactional(readOnly = true)
    public boolean unreadStatus(Long userId) {
        User user = findUserOrThrow(userId);
        return userNoticeRepository.existsUnreadByUser(user);
    }

    @Transactional
    public void markAsRead(Long userId, Long userNoticeId) {
        UserNotice notice = userNoticeRepository.findById(userNoticeId).orElseThrow(() -> new GeneralException(NoticeErrorCode._USER_NOTICE_NOT_FOUND));

        if (!notice.getUser().getUserId().equals(userId)) {
            throw new GeneralException(NoticeErrorCode._USER_NOTICE_FORBIDDEN);
        }

        notice.markAsRead();
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        User user = findUserOrThrow(userId);
        userNoticeRepository.markAllAsRead(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createNoticeNotification(NoticeCreatedEvent event) {
        List<User> pushUsers = userRepository.findValidPushUsers();

        List<UserNotice> notices = pushUsers.stream()
                .map(user -> UserNotice.create(
                        user,
                        NotificationAction.MOVE_NOTICE_DETAIL,
                        event.noticeId(),
                        event.title(),
                        event.content()
                )).toList();
        userNoticeRepository.saveAll(notices);
    }
}
