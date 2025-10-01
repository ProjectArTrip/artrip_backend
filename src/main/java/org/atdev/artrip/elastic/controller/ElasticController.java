package org.atdev.artrip.elastic.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.apipayload.code.ReasonDTO;
import org.atdev.artrip.global.apipayload.code.status.SuccessStatus;
import org.atdev.artrip.elastic.dto.EsSearchResponse;
import org.atdev.artrip.elastic.service.ElasticSearchService;
import org.atdev.artrip.elastic.service.ElasticExhibitIndexService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Slf4j
public class ElasticController {

    private final ElasticSearchService service;
    private final ElasticExhibitIndexService elasticExhibitIndexService;

    @Operation(summary = "최근 검색어 조회", description = "사용자의 최근 검색어를 조회합니다.")
    @GetMapping("/recent/{userId}")
    public List<String> getRecentKeywords(@PathVariable Long userId){
        return service.getRecentKeywords(userId);
    }

    @Operation(summary = "최근 검색어 저장", description = "사용자의 최근 검색어를 저장합니다.")
    @PostMapping("/{userId}")
    public ResponseEntity<ReasonDTO> saveRecentKeywords(@PathVariable Long userId, @RequestParam String keyword){
        service.saveKeyword(userId, keyword);
        return ResponseEntity.ok(SuccessStatus._OK.getReason());
    }

    @Operation(summary = "전시회 검색", description = "키워드로 전시회를 검색합니다.")
    @GetMapping("/exhibits")
    public List<EsSearchResponse> searchExhibits(@RequestParam String keyword){
        return elasticExhibitIndexService.searchExhibits(keyword);
    }
}
