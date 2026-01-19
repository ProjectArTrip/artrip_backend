package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.page.Criteria;
import org.atdev.artrip.global.page.PagingResponseDTO;
import org.atdev.artrip.controller.dto.request.CreateExhibitHallRequest;
import org.atdev.artrip.controller.dto.response.ExhibitHallListResponse;
import org.atdev.artrip.controller.dto.response.ExhibitHallResponse;
import org.atdev.artrip.controller.dto.request.UpdateExhibitHallRequest;
import org.atdev.artrip.service.AdminExhibitHallService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/exhibit-halls")
@RequiredArgsConstructor
@Tag(name = "Admin - ExhibitHall", description = "관리자 전시관 관리 API")
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
    public CommonResponse<PagingResponseDTO<ExhibitHallListResponse>> getExhibitHallList(Criteria cri) {

        PagingResponseDTO<ExhibitHallListResponse> result = adminExhibitHallService.getExhibitHallList(cri);

        return CommonResponse.onSuccess(result);
    }

    @Operation(summary = "전시관 상세 조회", description = "전시관 ID로 전시관 상세 조회")
    @GetMapping("/{exhibitHallId}")
    public CommonResponse<ExhibitHallResponse> getExhibitHall(@PathVariable Long exhibitHallId) {

        ExhibitHallResponse result = adminExhibitHallService.getExhibitHall(exhibitHallId);

        return CommonResponse.onSuccess(result);
    }

    @Operation(summary = "전시관 등록" )
    @PostMapping
    public CommonResponse<Long> createExhibitHall(@RequestBody CreateExhibitHallRequest request) {

        Long result = adminExhibitHallService.createExhibitHall(request);

        return CommonResponse.onSuccess(result);
    }

    @Operation(summary = "전시관 수정" )
    @PutMapping("/{exhibitHallId}")
    public CommonResponse<Long> updateExhibitHall(@PathVariable Long exhibitHallId, @RequestBody UpdateExhibitHallRequest request) {

        Long result = adminExhibitHallService.updateExhibitHall(exhibitHallId, request);

        return CommonResponse.onSuccess(result);
    }

    @Operation(summary = "전시관 삭제" )
    @DeleteMapping("/{exhibitHallId}")
    public CommonResponse<Void> deleteExhibitHall(@PathVariable Long exhibitHallId) {

        adminExhibitHallService.deleteExhibitHall(exhibitHallId);

        return CommonResponse.onSuccess(null);
    }

}
