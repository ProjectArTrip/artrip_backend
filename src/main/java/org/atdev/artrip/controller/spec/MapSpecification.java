package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.response.ExhibitMarkerListResponse;
import org.atdev.artrip.controller.dto.response.FilterCursorResponse;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.repository.dto.ExhibitMarkerDto;
import org.atdev.artrip.utils.CursorPagination;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MapSpecification {

    @Operation(summary = "클러스터링 전시 조회", description = "")
    public ResponseEntity<FilterCursorResponse> clusterExhibit(@RequestParam List<Long> ids,
                                                               @LoginUser Long userId,
                                                               @Valid @ParameterObject CursorPagination cursorPagination);


    @Operation(summary = "마커용 전시 일괄 조회", description = "etag 동일 시 - 304 , 다르면 - 다시 조회")
    public ResponseEntity<ExhibitMarkerListResponse> getMarkers(
            @RequestHeader(value = "If-None-Match", required = false) String etag);

    }
