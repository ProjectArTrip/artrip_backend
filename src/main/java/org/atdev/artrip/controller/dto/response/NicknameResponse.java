package org.atdev.artrip.controller.dto.response;


import lombok.*;
import org.atdev.artrip.service.dto.result.NicknameResult;


public record NicknameResponse(String nickName) {

    public static NicknameResponse from(NicknameResult result){
        return new NicknameResponse(result.nickName());
    }
}
