package org.atdev.artrip.infra.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;

class NotificationTemplateTest {

    @ParameterizedTest
    @EnumSource(value = NotificationTemplate.class, names = {"EXHIBIT_REPORT_RECEIVED", "EXHIBIT_REPORT_REGISTERED"})
    @DisplayName("제보 알림 템플릿은 제목 하나로 포맷 오류 없이 만들어진다")
    void formatWithoutError(NotificationTemplate template) {
        assertThatCode(() -> template.formatBody("전시 제목")).doesNotThrowAnyException();
    }
}
