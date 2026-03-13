package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.ClusterRequestDto;
import org.atdev.artrip.controller.dto.response.FilterCursorResponse;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.repository.dto.ExhibitMarkerDto;
import org.atdev.artrip.service.ExhibitService;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {

    private final ExhibitService exhibitService;

    @Operation(summary = "클러스터링 전시 조회", description = "")
    @GetMapping("/cluster")
    public ResponseEntity<FilterCursorResponse> clusterExhibit(@ModelAttribute ClusterRequestDto request,
                                                               @LoginUser Long userId,
                                                               @RequestParam(required = false) Long cursorId,
                                                               @RequestParam(required = false) LocalDate nextCursorDate,
                                                               @RequestParam(defaultValue = "20") int size){

        ExhibitFilterResult result = exhibitService.getClusterExhibit(request.ids(),nextCursorDate,cursorId,size,userId);

        return ResponseEntity.ok(FilterCursorResponse.from(result));
    }

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
