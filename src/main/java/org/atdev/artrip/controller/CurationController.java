package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.CurationDetailCursorResponse;
import org.atdev.artrip.controller.dto.response.CurationSummaryResponse;
import org.atdev.artrip.controller.spec.CurationSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.CurationService;
import org.atdev.artrip.service.dto.condition.CurationSearchCondition;
import org.atdev.artrip.service.dto.result.CurationDetailCursorResult;
import org.atdev.artrip.service.dto.result.CurationSummaryResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/curations")
public class CurationController implements CurationSpecification {

    private final CurationService curationService;

    @Override
    @GetMapping
    public ResponseEntity<CurationSummaryResponse> getSummary(
            @LoginUser Long userId,
            CurationSearchCondition condition) {

        CurationSummaryResult result = curationService.getSummaryCuration(userId, condition);
        return ResponseEntity.ok(CurationSummaryResponse.from(result));
    }

    @Override
    @GetMapping("/{curationId}")
    public ResponseEntity<CurationDetailCursorResponse> getDetail(
            @LoginUser Long userId,
            @PathVariable Long curationId,
            CursorPagination cursorPagination){

        CurationDetailCursorResult result = curationService.getCurationDetail(userId, curationId, cursorPagination);
        return ResponseEntity.ok(CurationDetailCursorResponse.from(result));
    }

}
