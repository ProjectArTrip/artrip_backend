package org.atdev.artrip.service.dto.command;

public record UserReadCommand(
        Long userId
) {
    public static UserReadCommand from(Long userId) {
        return new UserReadCommand(userId);
    }
}