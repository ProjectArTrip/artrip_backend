package org.atdev.artrip.controller.dto.response;


import lombok.Builder;
import org.atdev.artrip.service.dto.result.MypageResult;

@Builder
public record MypageResponse(
        String nickName,
        String profileImage,
        String email
) {

    public static MypageResponse from(MypageResult result){

        return MypageResponse.builder()
                .nickName(result.nickName())
                .profileImage(result.profileImage())
                .email(result.email())
                .build();
    }
}
