package org.atdev.artrip.infra.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTemplateTest {

    @Test
    @DisplayName("EXHIBIT_SYNC_WEEKLY는 새로 추가된 전시 건수를 %d 형식으로 본문에 채운다")
    void exhibitSyncWeekly_formatsNewCountIntoBody() {
        NotificationTemplate template = NotificationTemplate.EXHIBIT_SYNC_WEEKLY;

        assertThat(template.getTitle()).isEqualTo("이번 주 새로운 전시가 추가되었습니다.");
        assertThat(template.formatBody(7)).isEqualTo("7건의 새로운 전시가 추가되었어요. 지금 확인해보세요.");
    }

    @Test
    @DisplayName("EXHIBIT_REPORT_RECEIVED는 %s 포맷으로 전시명을 정상적으로 채운다 (회귀 테스트)")
    void exhibitReportReceived_formatsExhibitNameCorrectly() {
        NotificationTemplate template = NotificationTemplate.EXHIBIT_REPORT_RECEIVED;

        assertThat(template.formatBody("모네전")).isEqualTo("'모네전' 제보가 정상적으로 접수되었어요. 확인 후 등록해드릴게요.");
    }

    @Test
    @DisplayName("EXHIBIT_REPORT_REGISTERED는 %s 포맷으로 전시명을 정상적으로 채운다 (회귀 테스트)")
    void exhibitReportRegistered_formatsExhibitNameCorrectly() {
        NotificationTemplate template = NotificationTemplate.EXHIBIT_REPORT_REGISTERED;

        assertThat(template.formatBody("모네전")).isEqualTo("'모네전' 전시가 등록되었습니다. 지금 확인해보세요!");
    }
}