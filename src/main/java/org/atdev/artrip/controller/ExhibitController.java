package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.ExhibitDetailResponse;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.service.ExhibitService;
import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.controller.dto.response.FilterResponse;
import org.atdev.artrip.service.HomeService;
import org.atdev.artrip.controller.dto.response.RegionResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.HomeError;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exhibit")
public class ExhibitController {

    private final HomeService homeService;
    private final ExhibitService exhibitService;

    private Long getUserId(UserDetails userDetails) {
        return userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
    }
    @Operation(summary = "장르 조회", description = "키워드 장르 데이터 전체 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_GENRE_NOT_FOUND}
    )
    @GetMapping("/genre")
    public ResponseEntity<CommonResponse<List<String>>> getGenres(){
        List<String> genres = homeService.getAllGenres();
        return ResponseEntity.ok(CommonResponse.onSuccess(genres));
    }

    @Operation(summary = "전시 상세 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ExhibitDetailResponse>> getExhibit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ParameterObject ImageResizeRequest resize
            ){
      
        Long userId = getUserId(userDetails);
        ExhibitDetailResponse exhibit= exhibitService.getExhibitDetail(id, userId, resize);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibit));
    }


    @Operation(summary = "해외 국가 목록 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED}
    )
    @GetMapping("/overseas")
    public ResponseEntity<CommonResponse<List<String>>> getOverseas(){

        List<String> OverseasList = homeService.getOverseas();

        return ResponseEntity.ok(CommonResponse.onSuccess(OverseasList));
    }

    @Operation(summary = "국내 지역 목록 조회")//하드코딩
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED}
    )
    @GetMapping("/domestic")
    public ResponseEntity<CommonResponse<List<RegionResponse>>> getDomestic(){

        List<RegionResponse> response = homeService.getRegions();

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }


    @Operation(summary = "전시 조건 필터 전체 조회",description = "기간, 지역, 장르, 전시 스타일 필터 조회 - null 시 전체선택")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_INVALID_DATE_RANGE, HomeError._HOME_UNRECOGNIZED_REGION, HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @PostMapping("/filter")
    public ResponseEntity<FilterResponse<HomeListResponse>> getDomesticFilter(@RequestBody ExhibitFilterRequest dto,
                                                                              @RequestParam(required = false) Long cursor,
                                                                              @RequestParam(defaultValue = "20") Long size,
                                                                              @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        FilterResponse<HomeListResponse> exhibits = homeService.getFilterExhibit(dto, size, cursor,userId);

        return ResponseEntity.ok(exhibits);

    }
}
