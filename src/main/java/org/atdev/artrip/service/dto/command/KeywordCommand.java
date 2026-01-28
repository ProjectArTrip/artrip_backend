package org.atdev.artrip.service.dto.command;


import java.util.List;

public record KeywordCommand(
        List<String> keywords,
        Long userId
) {

}
