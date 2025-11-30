package org.atdev.artrip.domain.auth.web.dto;

import lombok.Data;

@Data
public class ReissueRequest {
    private String refreshToken;
}
