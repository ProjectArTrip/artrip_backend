package org.atdev.artrip.controller.dto.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.service.dto.result.SearchHistoryResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SearchHistoryResponse(
        Long searchHistoryId,
        String content,
        LocalDate createdAt
) {

    public static SearchHistoryResponse from(SearchHistoryResult result) {
        return new SearchHistoryResponse(
                result.searchHistoryId(),
                result.content(),
                result.createdAt()
        );
    }

    public static List<SearchHistoryResponse> fromList(List<SearchHistoryResult> results) {
        return results.stream()
                .map(SearchHistoryResponse::from)
                .toList();
    }
}
