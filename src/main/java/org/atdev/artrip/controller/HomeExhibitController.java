package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.ExhibitDetailResponse;
import org.atdev.artrip.controller.dto.response.GenreResponse;
import org.atdev.artrip.controller.spec.HomeExhibitSpecification;
import org.atdev.artrip.service.ExhibitService;
import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.controller.dto.response.FilterResponse;
import org.atdev.artrip.service.HomeService;
import org.atdev.artrip.controller.dto.response.RegionResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.HomeErrorCode;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.service.dto.result.ExhibitDetailResult;
import org.atdev.artrip.service.dto.command.ExhibitDetailCommand;
import org.atdev.artrip.service.dto.result.GenreResult;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exhibit")
public class HomeExhibitController implements HomeExhibitSpecification {

    private final HomeService homeService;
    private final ExhibitService exhibitService;

    private Long getUserId(UserDetails userDetails) {
        return userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
    }

    @Override
    @GetMapping("/genre")
    public ResponseEntity<List<GenreResponse>> getGenres(){

        List<GenreResult> genres = homeService.getAllGenres();

        return ResponseEntity.ok(GenreResponse.from(genres));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ExhibitDetailResponse> getExhibit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ParameterObject ImageResizeRequest resize
            ){

        ExhibitDetailCommand query = ExhibitDetailCommand.of(id, getUserId(userDetails), resize.getW(), resize.getH(), resize.getF());
        ExhibitDetailResult result = exhibitService.getExhibitDetail(query);

        return ResponseEntity.ok(ExhibitDetailResponse.from(result));
    }

    @Operation(summary = "해외 국가 목록 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED}
    )
    @GetMapping("/overseas")
    public ResponseEntity<CommonResponse<List<String>>> getOverseas(){

        List<String> OverseasList = homeService.getOverseas();

        return ResponseEntity.ok(CommonResponse.onSuccess(OverseasList));
    }

    @Operation(summary = "국내 지역 목록 조회")//하드코딩
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED}
    )
    @GetMapping("/domestic")
    public ResponseEntity<CommonResponse<List<RegionResponse>>> getDomestic(){

        List<RegionResponse> response = homeService.getRegions();

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Operation(summary = "전시 조건 필터 전체 조회",description = "기간, 지역, 장르, 전시 스타일 필터 조회 - null 시 전체선택")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_INVALID_DATE_RANGE, HomeErrorCode._HOME_UNRECOGNIZED_REGION, HomeErrorCode._HOME_EXHIBIT_NOT_FOUND}
    )
    @PostMapping("/filter")
    public ResponseEntity<FilterResponse> getDomesticFilter(@RequestBody ExhibitFilterRequest dto,
                                                            @RequestParam(required = false) Long cursor,
                                                            @RequestParam(defaultValue = "20") Long size,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        FilterResponse exhibits = homeService.getFilterExhibit(dto, size, cursor,userId);

        return ResponseEntity.ok(exhibits);

    }

}
