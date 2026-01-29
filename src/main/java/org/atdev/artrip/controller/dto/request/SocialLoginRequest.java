package org.atdev.artrip.controller.dto.request;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String provider;
    private String idToken;
}
