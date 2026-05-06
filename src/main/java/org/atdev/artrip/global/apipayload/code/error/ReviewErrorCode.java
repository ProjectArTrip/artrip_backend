package org.atdev.artrip.global.apipayload.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {

    _TOO_MANY_REVIEW_IMAGES(HttpStatus.BAD_REQUEST,"REVIEW400-BAD_REQUEST","최대 4장까지 업로드가 가능합니다"),
    _REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404-NOT_FOUND", "리뷰 정보를 찾을 수 없습니다."),
    _REVIEW_USER_NOT_ROLE(HttpStatus.FORBIDDEN, "REVIEW403-NO_PERMISSION", "해당 유저에게 리뷰 수정권한이 없습니다."),

    _BAD_WORD_INCLUDED(HttpStatus.BAD_REQUEST, "REVIEW400-BAD_WORD_INCLUDED", "금칙어가 포함되어 있습니다."),
    _REVIEW_ALREADY_REJECTED(HttpStatus.CONFLICT, "REVIEW409-ALREADY_REJECTED", "이미 반려된 리뷰입니다."),
    _REVIEW_ALREADY_APPROVED(HttpStatus.CONFLICT, "REVIEW-409-ALREADY_APPROVED", "이미 승인되 리뷰 입니다."),
    _REVIEW_NOT_PENDING(HttpStatus.CONFLICT, "REVIEW409-NOT_PENDING", "승인 대기 상태의 리뷰만 처리할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
