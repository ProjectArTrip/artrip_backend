package org.atdev.artrip.domain.auth.web.dto;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String provider;
    private String idToken;
}
