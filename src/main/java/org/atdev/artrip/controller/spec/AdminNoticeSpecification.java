package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.request.AdminCreateNoticeRequest;
import org.atdev.artrip.controller.dto.request.AdminUpdateNoticeRequest;
import org.atdev.artrip.controller.dto.response.AdminNoticeCreateResponse;
import org.atdev.artrip.global.apipayload.code.status.FcmErrorCode;
import org.atdev.artrip.global.apipayload.code.status.NoticeErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdminNoticeSpecification {

    @Operation(summary = "관리자 공지사항 생성")
    @ApiErrorResponses(
            user = {UserErrorCode._USER_NOT_FOUND},
            fcmToken = {FcmErrorCode._INVALID_REQUEST_PATTERN, FcmErrorCode._FCM_SERVER_ERROR}
    )
    public ResponseEntity<AdminNoticeCreateResponse> createNotice(
            @LoginUser Long userId,
            @RequestBody AdminCreateNoticeRequest request
            );

    @Operation(summary = "관리자 공지사항 수정")
    @ApiErrorResponses(
            notis = {NoticeErrorCode._NOTICE_NOT_FOUND},
            user = {UserErrorCode._USER_NOT_FOUND, UserErrorCode._USER_FORBIDDEN}
    )
    public ResponseEntity<Void> updateNotice(
            @LoginUser Long userId,
            @PathVariable Long noticeId,
            @Valid @RequestBody AdminUpdateNoticeRequest request
    );

    @Operation(summary = "관리자 공지사항 삭제")
    @ApiErrorResponses(
            notis = {NoticeErrorCode._NOTICE_NOT_FOUND},
            user = {UserErrorCode._USER_FORBIDDEN, UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long noticeId,
            @LoginUser Long userId
    );

}
