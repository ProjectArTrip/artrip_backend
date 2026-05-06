package org.atdev.artrip.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewNotificationMessage {
    REVIEW_APPROVED(
            "리뷰가 승인되었습니다.",
            "%s 전시에 작성하신 리뷰가 승인되어 전시 상세페이지에 노출됩니다."
    ),
    REVIEW_DELETED_BY_ADMIN(
            "리뷰가 삭제되었습니다.",
            "작성하신 리뷰가 운영 정책에 따라 삭제되었습니다."
    ),
    REVIEW_REJECTED_POLICY(
            "리뷰가 반려되었습니다.",
            "운영 정책에 따라 리뷰가 반려되었습니다. 리뷰 내용을 수정해 다시 등록해주세요."
    ),
    REVIEW_REJECTED_BANNED_WORD(
            "리뷰가 반려되었습니다.",
            "부적절한 표현이 포함되어 리뷰가 반려되었습니다. 내용을 수정해 다시 등록해 주세요."
    );

    private final String title;
    private final String body;

    public String formatBody(Object... args) {
        return body.formatted(args);
    }

}
