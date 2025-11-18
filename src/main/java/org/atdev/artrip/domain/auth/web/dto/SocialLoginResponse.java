package org.atdev.artrip.domain.auth.web.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocialLoginResponse {
    private String accessToken;
    private String refreshToken;
}