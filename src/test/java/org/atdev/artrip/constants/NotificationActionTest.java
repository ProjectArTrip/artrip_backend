package org.atdev.artrip.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationActionTest {

    @Test
    @DisplayName("MOVE_EXHIBIT_LIST의 action 코드는 MOVE_EXHIBIT_LIST 이다")
    void moveExhibitList_hasExpectedActionCode() {
        assertThat(NotificationAction.MOVE_EXHIBIT_LIST.getAction()).isEqualTo("MOVE_EXHIBIT_LIST");
    }

    @Test
    @DisplayName("MOVE_EXHIBIT_REVIEW_DETAIL의 action 코드는 MOVE_EXHIBIT_DETAIL과 동일하다")
    void moveExhibitReviewDetail_sharesActionCodeWithExhibitDetail() {
        assertThat(NotificationAction.MOVE_EXHIBIT_REVIEW_DETAIL.getAction())
                .isEqualTo(NotificationAction.MOVE_EXHIBIT_DETAIL.getAction());
        assertThat(NotificationAction.MOVE_EXHIBIT_REVIEW_DETAIL.getAction()).isEqualTo("MOVE_EXHIBIT_DETAIL");
    }

    @Test
    @DisplayName("새로 추가된 enum 상수는 존재하며 고유한 이름을 가진다")
    void newConstants_existWithDistinctNames() {
        assertThat(NotificationAction.valueOf("MOVE_EXHIBIT_LIST")).isEqualTo(NotificationAction.MOVE_EXHIBIT_LIST);
        assertThat(NotificationAction.valueOf("MOVE_EXHIBIT_REVIEW_DETAIL")).isEqualTo(NotificationAction.MOVE_EXHIBIT_REVIEW_DETAIL);
        assertThat(NotificationAction.MOVE_EXHIBIT_LIST).isNotEqualTo(NotificationAction.MOVE_EXHIBIT_REVIEW_DETAIL);
    }
}