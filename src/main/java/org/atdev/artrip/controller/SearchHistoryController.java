package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.SearchHistoryListResponse;
import org.atdev.artrip.controller.spec.SearchHistorySpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.SearchHistoryService;
import org.atdev.artrip.service.dto.command.SearchHistoryCommand;
import org.atdev.artrip.service.dto.result.SearchHistoryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search-history")
@RequiredArgsConstructor
public class SearchHistoryController implements SearchHistorySpecification {

    private final SearchHistoryService searchHistoryService;

    @Override
    @GetMapping
    public ResponseEntity<SearchHistoryListResponse> getRecentSearchHistory(@LoginUser Long userId) {

        SearchHistoryCommand command = SearchHistoryCommand.from(userId);

        List<SearchHistoryResult> results = searchHistoryService.getRecentSearchHistory(command);

        return ResponseEntity.ok(SearchHistoryListResponse.from(results));
    }

    @Override
    @DeleteMapping("/{searchHistoryId}")
    public ResponseEntity<Void> deleteSearchHistory(@LoginUser Long userId, @Valid @PathVariable Long searchHistoryId) {
        SearchHistoryCommand command = SearchHistoryCommand.of(userId,searchHistoryId);
        searchHistoryService.deleteSearchHistory(command);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> deleteAllSearchHistory(@LoginUser Long userId) {

        searchHistoryService.deleteAllSearchHistory(userId);

        return ResponseEntity.noContent().build();
    }
}
