package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.FilterCursorResponse;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.repository.dto.ExhibitMarkerDto;
import org.atdev.artrip.service.ExhibitService;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {

    private final ExhibitService exhibitService;

    @Operation(summary = "클러스터링 전시 조회", description = "")
    @GetMapping("/cluster")
    public ResponseEntity<FilterCursorResponse> clusterExhibit(@RequestParam List<Long> ids,
                                                               @LoginUser Long userId,
                                                               @RequestParam(required = false) Long cursorId,
                                                               @RequestParam(defaultValue = "20") int size){

        ExhibitFilterResult result = exhibitService.getClusterExhibit(ids,cursorId,size,userId);

        return ResponseEntity.ok(FilterCursorResponse.from(result));
    }

    @Operation(summary = "마커용 전시 일괄 조회", description = "etag 동일 시 - 304 , 다르면 - 다시 조회")
    @GetMapping("/exhibits/markers")
    public ResponseEntity<List<ExhibitMarkerDto>> getMarkers(
            @RequestHeader(value = "If-None-Match", required = false) String etag) {

        String currentEtag = exhibitService.getMarkerEtag();

        if (currentEtag.equals(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(currentEtag)
                    .build();
        }

        List<ExhibitMarkerDto> markers = exhibitService.getMarkers();

        return ResponseEntity.ok()
                .eTag(currentEtag)
                .body(markers);
    }
}
