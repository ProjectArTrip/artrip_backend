package org.atdev.artrip.domain.auth.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SocialUserInfo {
    private String email;
    private String nickname;
    private String ProviderId;
}