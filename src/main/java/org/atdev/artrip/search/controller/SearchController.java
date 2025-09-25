package org.atdev.artrip.search.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.search.document.ExhibitDocument;
import org.atdev.artrip.search.dto.ExhibitResponse;
import org.atdev.artrip.search.service.ElasticSearchService;
import org.atdev.artrip.search.service.ExhibitIndexService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final ElasticSearchService service;
    private final ExhibitIndexService exhibitIndexService;

    @GetMapping("/recent/{userId}")
    public List<String> getRecentKeywords(@PathVariable Long userId){
        return service.getRecentKeywords(userId);
    }

    @PostMapping("/{userId}")
    public void saveRecentKeywords(@PathVariable Long userId, @RequestParam String keyword){
        service.saveKeyword(userId, keyword);
    }

    @GetMapping("/exhibits")
    public List<ExhibitResponse> searchExhibits(@RequestParam String keyword){
        return exhibitIndexService.searchExhibits(keyword);
    }
}
