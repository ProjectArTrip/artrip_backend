package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
public record NicknameRequest (
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "2~10자 사이여야 합니다.")
        String NickName
){
}
