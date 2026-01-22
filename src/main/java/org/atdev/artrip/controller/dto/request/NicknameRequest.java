package org.atdev.artrip.controller.dto.request;

import lombok.*;
import org.atdev.artrip.service.dto.command.NicknameCommand;

@Builder
public record NicknameRequest (
        String NickName
){
    public NicknameCommand toCommand(NicknameRequest request,Long userId){

        return new NicknameCommand(request.NickName(),userId);
    }
}
