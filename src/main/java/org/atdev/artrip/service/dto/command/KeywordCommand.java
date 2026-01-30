package org.atdev.artrip.service.dto.command;

import lombok.Builder;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.keyword.Keyword;

import java.util.List;

public record KeywordCommand(
        List<String> keywords,
        Long userId
) {

}
