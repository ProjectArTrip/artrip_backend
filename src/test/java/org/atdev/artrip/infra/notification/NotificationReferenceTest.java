package org.atdev.artrip.infra.notification;

import org.atdev.artrip.constants.NotificationAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationReferenceTest {

    @Test
    @DisplayName("exhibitList()는 MOVE_EXHIBIT_LIST 액션에 referenceId 없이 생성된다")
    void exhibitList_createsReferenceWithoutId() {
        NotificationReference reference = NotificationReference.exhibitList();

        assertThat(reference.action()).isEqualTo(NotificationAction.MOVE_EXHIBIT_LIST);
        assertThat(reference.referenceId()).isNull();
    }

    @Test
    @DisplayName("exhibitList()의 toFcmData()는 action만 포함하고 referenceId는 포함하지 않는다")
    void exhibitList_toFcmData_containsOnlyAction() {
        NotificationReference reference = NotificationReference.exhibitList();

        assertThat(reference.toFcmData())
                .containsEntry("action", "MOVE_EXHIBIT_LIST")
                .doesNotContainKey("referenceId")
                .hasSize(1);
    }
}