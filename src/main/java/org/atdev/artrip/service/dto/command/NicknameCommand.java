package org.atdev.artrip.service.dto.command;

public record NicknameCommand(
        String nickName,
        Long userId
) {
}
