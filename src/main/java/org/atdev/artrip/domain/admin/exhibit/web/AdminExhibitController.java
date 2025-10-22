package org.atdev.artrip.domain.admin.exhibit.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.admin.common.dto.Criteria;
import org.atdev.artrip.domain.admin.common.dto.PagingResponseDTO;
import org.atdev.artrip.domain.admin.exhibit.dto.ExhibitListResponse;
import org.atdev.artrip.domain.admin.exhibit.service.AdminExhibitService;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/exhibits")
@Slf4j
@Tag(name = "Admin - Exhibit", description = "관리자 전시 관리 API")
public class AdminExhibitController {

    private final AdminExhibitService adminExhibitService;

    @Operation(summary = "전시 목록 조회", description = "전시 목록 페이징 조회 및 검색어 필터링")
    @GetMapping
    public ApiResponse<PagingResponseDTO<ExhibitListResponse>> getExhibitList(Criteria cri) {
        log.info("Admin getting exhibit list: {}", cri);

        PagingResponseDTO<ExhibitListResponse> result = adminExhibitService.getExhibitList(cri);

        return ApiResponse.onSuccess(result);

    }

}
