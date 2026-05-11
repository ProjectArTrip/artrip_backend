package org.atdev.artrip.infra.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationTemplate {
    REVIEW_APPROVED(
            "리뷰가 승인되었습니다.",
            "%s 전시에 작성하신 리뷰가 승인되어 전시 상세페이지에 노출됩니다."
    ),
    REVIEW_DELETED_BY_ADMIN(
            "리뷰가 삭제되었습니다.",
            "작성하신 리뷰가 운영 정책에 따라 삭제되었습니다."
    ),
    REVIEW_REJECTED(
            "리뷰가 반려되었습니다.",
            "[%s] 반려되었습니다.\n[%s] %s"
    );

    private final String title;
    private final String body;

    public String formatBody(Object... args) {
        return body.formatted(args);
    }

}
