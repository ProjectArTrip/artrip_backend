package org.atdev.artrip.domain.admin.exhibit.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.admin.common.dto.Criteria;
import org.atdev.artrip.domain.admin.common.dto.PagingResponseDTO;
import org.atdev.artrip.domain.admin.exhibit.dto.CreateExhibitRequest;
import org.atdev.artrip.domain.admin.exhibit.dto.ExhibitAdminResponse;
import org.atdev.artrip.domain.admin.exhibit.dto.ExhibitListResponse;
import org.atdev.artrip.domain.admin.exhibit.dto.UpdateExhibitRequest;
import org.atdev.artrip.domain.admin.exhibit.service.AdminExhibitService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/exhibits")
@Slf4j
@Tag(name = "Admin - Exhibit", description = "관리자 전시 관리 API")
public class AdminExhibitController {

    private final AdminExhibitService adminExhibitService;

    @Operation(summary = "전시 전체 및 검색 조회", description = """
            ### 1. 전체 목록 조회
            - searchValue를 비워두면 전체 전시 조회
            
            ### 2. 검색
            - searchValue에 검색어 입력 시 전시 이름에서 검색
            
            ### 3. 페이징
            - sortField: exhibitId, title, createdAt
            - sortDirection: ASC, DESC
            """
    )
    @GetMapping
    public CommonResponse<PagingResponseDTO<ExhibitListResponse>> getExhibitList(Criteria cri) {
        log.info("Admin getting exhibit list: {}", cri);

        PagingResponseDTO<ExhibitListResponse> result = adminExhibitService.getExhibitList(cri);

        return CommonResponse.onSuccess(result);
    }

    @Operation(summary = "전시 상세 조회", description = "특정 전시의 상세 정보를 조회합니다.")
    @GetMapping("/{exhibitId}")
    public CommonResponse<ExhibitAdminResponse> getExhibit(@PathVariable Long exhibitId) {
        log.info("Admin getting exhibit : {}", exhibitId);

        ExhibitAdminResponse result = adminExhibitService.getExhibit(exhibitId);

        return CommonResponse.onSuccess(result);
    }

    @Operation(summary = "전시 등록", description = "새로운 전시를 등록합니다.")
    @PostMapping
    public CommonResponse<Long> createExhibit(@RequestBody CreateExhibitRequest request) {
        log.info("Admin creating exhibit: title = {}", request.getTitle());

        Long exhibitId = adminExhibitService.createExhibit(request);

        return CommonResponse.onSuccess(exhibitId);
    }

    @Operation(summary = "전시 수정", description = "특정 전시를 수정합니다.")
    @PutMapping("/{exhibitId}")
    public CommonResponse<Long> updateExhibit(@PathVariable Long exhibitId, @RequestBody UpdateExhibitRequest request){
    log.info("Admin updating exhibit: {}", request.getTitle());

    Long updatedId = adminExhibitService.updateExhibit(exhibitId, request);

    return CommonResponse.onSuccess(updatedId);
    }

    @Operation(summary = "전시 삭제", description = "특정 전시를 삭제합니다.")
    @DeleteMapping("/{exhibitId}")
    public CommonResponse<Void> deleteExhibit(@PathVariable Long exhibitId) {
        log.info("Admin deleting exhibit: {}", exhibitId);

        adminExhibitService.deleteExhibit(exhibitId);
        return CommonResponse.onSuccess(null);
    }
}
