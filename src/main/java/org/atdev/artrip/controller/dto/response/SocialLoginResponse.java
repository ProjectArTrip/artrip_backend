package org.atdev.artrip.controller.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocialLoginResponse {
    private String accessToken;
    private String refreshToken;
    private boolean isFirstLogin;
}