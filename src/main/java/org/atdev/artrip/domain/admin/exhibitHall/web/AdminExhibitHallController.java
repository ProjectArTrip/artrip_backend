package org.atdev.artrip.domain.admin.exhibitHall.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.admin.common.dto.Criteria;
import org.atdev.artrip.domain.admin.common.dto.PagingResponseDTO;
import org.atdev.artrip.domain.admin.exhibitHall.dto.CreateExhibitHallRequest;
import org.atdev.artrip.domain.admin.exhibitHall.dto.ExhibitHallListResponse;
import org.atdev.artrip.domain.admin.exhibitHall.dto.ExhibitHallResponse;
import org.atdev.artrip.domain.admin.exhibitHall.dto.UpdateExhibitHallRequest;
import org.atdev.artrip.domain.admin.exhibitHall.service.AdminExhibitHallService;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/exhibit-halls")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AdminExhibitHall", description = "관리자 전시관 관리 API")
public class AdminExhibitHallController {

    private final AdminExhibitHallService adminExhibitHallService;

    @Operation(summary = "전시관 전체 및 검색 조회", description = """
            ### 1. 전체 목록 조회
            - searchValue를 비워두면 전체 전시관 조회
            
            ### 2. 검색
            - searchValue에 검색어 입력 시 전시홀 이름에서 검색
            
            ### 3. 페이징
            - sortField: exhibitHallId, name, country, region
            - sortDirection: ASC, DESC
            """
    )
    @GetMapping
    public ApiResponse<PagingResponseDTO<ExhibitHallListResponse>> getExhibitHallList(Criteria cri) {
        log.info("Admin getting exhibit hall : {}" , cri);

        PagingResponseDTO<ExhibitHallListResponse> result = adminExhibitHallService.getExhibitHallList(cri);

        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "전시관 상세 조회", description = "전시관 ID로 전시관 상세 조회")
    @GetMapping("/{exhibitHallId}")
    public ApiResponse<ExhibitHallResponse> getExhibitHall(@PathVariable Long exhibitHallId) {
        log.info("Admin getting exhibit hall : {}" , exhibitHallId);

        ExhibitHallResponse result = adminExhibitHallService.getExhibitHall(exhibitHallId);

        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "전시관 등록" )
    @PostMapping
    public ApiResponse<Long> createExhibitHall(@RequestBody CreateExhibitHallRequest request) {
        log.info("Admin creating exhibit hall : {}" , request);

        Long result = adminExhibitHallService.createExhibitHall(request);

        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "전시관 수정" )
    @PutMapping("/{exhibitHallId}")
    public ApiResponse<Long> updateExhibitHall(@PathVariable Long exhibitHallId, @RequestBody UpdateExhibitHallRequest request) {
        log.info("Admin updating exhibit hall : {}, {}", exhibitHallId, request);

        Long result = adminExhibitHallService.updateExhibitHall(exhibitHallId, request);

        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "전시관 삭제" )
    @DeleteMapping("/{exhibitHallId}")
    public ApiResponse<Void> deleteExhibitHall(@PathVariable Long exhibitHallId) {
        log.info("Admin deleting exhibit hall : {}" , exhibitHallId);

        adminExhibitHallService.deleteExhibitHall(exhibitHallId);

        return ApiResponse.onSuccess(null);
    }

}
