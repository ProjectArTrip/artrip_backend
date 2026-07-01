package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.controller.dto.response.AdminNoticeListResponse;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.notice.Notice;
import org.atdev.artrip.domain.notice.event.NoticeCreatedEvent;
import org.atdev.artrip.global.apipayload.code.error.NoticeErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.code.status.NoticeStatusCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.NoticeRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.AdminNoticeCreateCommand;
import org.atdev.artrip.service.dto.command.AdminNoticeUpdateCommand;
import org.atdev.artrip.service.dto.result.AdminNoticeCreateResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AdminNoticeCreateResult createNotice(AdminNoticeCreateCommand command) {

        User admin = validUser(command.userId());

        Notice notice = Notice.create(admin, command.title(), command.content());
        noticeRepository.save(notice);

        long pushUserCount = userRepository.countValidPushUsers();

        eventPublisher.publishEvent(new NoticeCreatedEvent(notice.getNoticeId(), command.title(), command.content()));

        String message = pushUserCount == 0
                ? NoticeStatusCode.NOTICE_CREATE_NO_PUSH_TARGET.getMessage()
                : NoticeStatusCode.NOTICE_CREATE_WITH_PUSH.format(pushUserCount);


        return AdminNoticeCreateResult.of(notice.getNoticeId(), pushUserCount, message);
    }

    @Transactional
    public void updateNotice(AdminNoticeUpdateCommand command) {
        validUser(command.userId());

        Notice notice = noticeRepository.findById(command.noticeId()).orElseThrow(() -> new GeneralException(NoticeErrorCode._NOTICE_NOT_FOUND));

        notice.update(command.title(), command.content());
    }

    @Transactional
    public void deleteNotice(Long userId, Long noticeId) {
        validUser(userId);

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new GeneralException(NoticeErrorCode._NOTICE_NOT_FOUND));

        noticeRepository.delete(notice);
    }


    private User validUser(Long userId) {
        User admin = userRepository.findById(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        if (admin.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }

        return admin;
    }

    @Transactional(readOnly = true)
    public Page<AdminNoticeListResponse> listNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable)
                .map(AdminNoticeListResponse::from);
    }
}
