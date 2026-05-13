package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record PushEnabledRequest(
        @NotNull(message = "푸시 알림 허용 여부는 필수입니다.")
        Boolean enabled
) {
}
