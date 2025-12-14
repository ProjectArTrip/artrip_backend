package org.atdev.artrip.domain.auth.web.dto.request;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String provider;
    private String idToken;
}
