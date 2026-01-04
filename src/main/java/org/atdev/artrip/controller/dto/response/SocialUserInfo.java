package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SocialUserInfo {
    private String email;
    private String Nickname;
    private String ProviderId;
}