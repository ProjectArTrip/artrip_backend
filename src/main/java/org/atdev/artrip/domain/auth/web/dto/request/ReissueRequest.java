package org.atdev.artrip.domain.auth.web.dto.request;

import lombok.Data;

@Data
public class ReissueRequest {
    private String refreshToken;
}
