package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.atdev.artrip.service.dto.command.AdminNoticeCreateCommand;

public record AdminCreateNoticeRequest(

        @NotNull(message = "공지 사항 제목")
        @Size(max = 20, message = "20자 이하로 작성해주세요")
        String title,

        @NotNull(message = "공지 사항 내용")
        @Size(max = 1000, message = "1000자 이하로 작성해주세요")
        String content
) {

    public static AdminNoticeCreateCommand toCommand(long userId, String title, String content) {
        return new AdminNoticeCreateCommand(userId, title, content);
    }
}
