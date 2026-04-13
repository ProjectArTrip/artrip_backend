package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.ReadStatusResponse;
import org.atdev.artrip.controller.dto.response.UserNoticeCursorResponse;
import org.atdev.artrip.controller.spec.UserNoticeSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.UserNoticeService;
import org.atdev.artrip.service.dto.result.UserNoticeCursorResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class UserNoticeController implements UserNoticeSpecification {

    private final UserNoticeService userNoticeService;

    @Override
    @GetMapping
    public ResponseEntity<UserNoticeCursorResponse> getNotifications(
            @LoginUser Long userId,
            CursorPagination pagination
    ) {
        UserNoticeCursorResult result = userNoticeService.getNotifications(userId, pagination);
        return ResponseEntity.ok(UserNoticeCursorResponse.from(result));
    }

    @Override
    @GetMapping("/read-status")
    public ResponseEntity<ReadStatusResponse> getUnreadStatus(
            @LoginUser Long userId
    ) {
        boolean status = userNoticeService.unreadStatus(userId);
        return ResponseEntity.ok(ReadStatusResponse.of(status));
    }

    @Override
    @PostMapping("/{userNoticeId}/read")
    public ResponseEntity<Void> markAsRead(
            @LoginUser Long userId,
            @PathVariable Long userNoticeId
    ) {
        userNoticeService.markAsRead(userId, userNoticeId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @LoginUser Long userId
    ) {
        userNoticeService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}
