package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.CurationDetailCursorResponse;
import org.atdev.artrip.controller.dto.response.CurationSummaryListResponse;
import org.atdev.artrip.controller.spec.CurationSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.CurationService;
import org.atdev.artrip.service.dto.result.CurationDetailCursorResult;
import org.atdev.artrip.service.dto.result.CurationSummaryListResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/curations")
public class CurationController implements CurationSpecification {

    private final CurationService curationService;

    @Override
    @GetMapping("/home")
    public ResponseEntity<CurationSummaryListResponse> getSummary(
            @LoginUser Long userId,
            @RequestParam(required = false) String country) {

        CurationSummaryListResult result = curationService.getSummaryCuration(userId, country);
        return ResponseEntity.ok(CurationSummaryListResponse.from(result));
    }

    @Override
    @GetMapping("/detail/{curationId}")
    public ResponseEntity<CurationDetailCursorResponse> getDetail(
            @LoginUser Long userId,
            @PathVariable Long curationId,
            CursorPagination cursorPagination){

        CurationDetailCursorResult result = curationService.getCurationDetail(userId, curationId, cursorPagination);
        return ResponseEntity.ok(CurationDetailCursorResponse.from(result));
    }

}
