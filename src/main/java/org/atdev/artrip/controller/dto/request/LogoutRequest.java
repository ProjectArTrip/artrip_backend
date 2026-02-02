package org.atdev.artrip.controller.dto.request;

public record LogoutRequest(
        String accessToken,
        String refreshToken
) {
}
