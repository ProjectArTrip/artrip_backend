package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MypageResponse {

    private String nickName;
    private String profileImage;
    private String email;

}
