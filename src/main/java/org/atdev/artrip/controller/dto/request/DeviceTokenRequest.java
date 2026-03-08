package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeviceTokenRequest(
        @NotBlank(message = "FCM Token은 필수 입니다.")
        String token
){
}
