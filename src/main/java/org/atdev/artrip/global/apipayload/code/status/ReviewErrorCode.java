package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {

    _TOO_MANY_REVIEW_IMAGES(HttpStatus.BAD_REQUEST,"REVIEW400-BAD_REQUEST","최대 4장까지 업로드가 가능합니다"),
    _REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404-NOT_FOUND", "리뷰 정보를 찾을 수 없습니다."),
    _REVIEW_USER_NOT_FOUND(HttpStatus.FORBIDDEN, "REVIEW403-NO_PERMISSION", "해당 유저에게 리뷰 수정권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
