package org.atdev.artrip.service.dto.command;

public record AdminNoticeCreateCommand(
        long userId,
        String title,
        String content
){

}
