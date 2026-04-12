package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.response.ReadStatusResponse;
import org.atdev.artrip.controller.dto.response.UserNoticeCursorResponse;
import org.atdev.artrip.global.apipayload.code.status.NoticeErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.utils.CursorPagination;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserNoticeSpecification {

    @Operation(summary = "알림 목록 조회")
    @ApiErrorResponses(user = {UserErrorCode._USER_NOT_FOUND})
    ResponseEntity<UserNoticeCursorResponse> getNotifications(@LoginUser Long userId, @ParameterObject CursorPagination pagination);

    @Operation(summary = "안읽은 알림 여부 조회",
            description = """ 
                    true: 읽지 않은 알림이 1개 이상일 경우
                    false: 모든 알림 읽음
                    """)
    @ApiErrorResponses(user = {UserErrorCode._USER_NOT_FOUND})
    ResponseEntity<ReadStatusResponse> getUnreadStatus(@LoginUser Long userId);

    @Operation(summary = "개별 읽음 처리")
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND},
            notis = {NoticeErrorCode._USER_NOTICE_NOT_FOUND, NoticeErrorCode._USER_NOTICE_FORBIDDEN})
    ResponseEntity<Void> markAsRead(@LoginUser Long userId, @PathVariable Long userNoticeId);

    @Operation(summary = "전체 알림 읽음 처리")
    @ApiErrorResponses(user = {UserErrorCode._USER_NOT_FOUND})
    ResponseEntity<Void> markAllAsRead(@LoginUser Long userId);
}
