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
    ),
    EXHIBIT_NEW_MULTIPLE(
            "새로운 전시가 등록되었어요.",
            "'%s' 외 %d건의 새로운 전시를 확인해보세요."

    ),
    EXHIBIT_NEW_SINGLE(
            "새로운 전시가 등록되었어요",
            "오늘 새롭게 공개된 전시가 있어요. %s의 '%s'을 지금 확인해보세요."
    ),
    EXHIBIT_NEW_PERSONALIZED(
            "%s님 취향에 맞는 새로운 전시가 등록되었어요",
            "회원님의 관심 장르와 스타일에 맞는 전시가 새로 공개되었어요. %s의 '%s'을 지금 확인해보세요."
    ),
    EXHIBIT_REPORT_RECEIVED(
            "전시 제보가 접수되었어요.",
            "'%' 제보가 정상적으로 접수되었어요. 확인 후 등록해드릴게요."
    ),
    EXHIBIT_REPORT_REGISTERED(
            "제보하신 전시가 등록되었습니다.",
            "'%' 전시가 등록되었습니다. 지금 확인해보세요!"
    )
    ;

    private final String title;
    private final String body;

    public String formatTitle(Object... args) {
        return String.format(title, args);
    }

    public String formatBody(Object... args) {
        return body.formatted(args);
    }
}
