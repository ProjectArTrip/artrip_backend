package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.NotificationAction;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.notice.Notice;
import org.atdev.artrip.domain.notice.UserNotice;
import org.atdev.artrip.global.apipayload.code.error.NoticeErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.fcm.NotificationSingleDispatch;
import org.atdev.artrip.domain.notice.event.NoticeCreatedEvent;
import org.atdev.artrip.infra.notification.NotificationMessage;
import org.atdev.artrip.repository.NoticeRepository;
import org.atdev.artrip.repository.UserNoticeRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.result.UserNoticeCursorResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher publisher;
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public UserNoticeCursorResult getNotifications(Long userId, CursorPagination pagination, NotificationAction action) {
        User user = findUserOrThrow(userId);

        Slice<UserNotice> slice = userNoticeRepository.findAllByUser(user, pagination.cursor(), action ,PageRequest.of(0, pagination.size().intValue()));

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

    @Transactional
    public void sendSingleFcmToken(Long userId, NotificationSingleDispatch command) {
        User admin = findUserOrThrow(userId);

        if(admin.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }

        Notice notice = Notice.create(admin, command.title(), command.body());
        noticeRepository.save(notice);

        publisher.publishEvent(new NoticeCreatedEvent(notice.getNoticeId(), command.title(), command.body()));
    }

    public void saveSingle(Long userId, NotificationMessage message) {
        User user = findUserOrThrow(userId);

        userNoticeRepository.save(UserNotice.create(
                user,
                message.reference().action(),
                message.reference().referenceId(),
                message.title(),
                message.body()
        ));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void broadcast(NotificationMessage message) {
        List<User> pushUsers = userRepository.findValidPushUsers();
        List<UserNotice> notices = pushUsers.stream()
                .map(user -> UserNotice.create(
                        user,
                        message.reference().action(),
                        message.reference().referenceId(),
                        message.title(),
                        message.body()
                )).toList();
        userNoticeRepository.saveAll(notices);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
    }
}
