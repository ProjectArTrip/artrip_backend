package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.service.ExhibitService;
import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.service.HomeService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.HomeErrorCode;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exhibits")
public class ExhibitController {

    private final HomeService homeService;
    private final ExhibitService exhibitService;
    private final S3Service s3Service;

    @Operation(summary = "장르 조회", description = "키워드 장르 데이터 전체 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_GENRE_NOT_FOUND}
    )
    @GetMapping("/genre")
    public ResponseEntity<CommonResponse<List<String>>> getGenres(){
        List<String> genres = homeService.getAllGenres();
        return ResponseEntity.ok(CommonResponse.onSuccess(genres));
    }

    @Operation(summary = "전시 상세 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ExhibitDetailResponse>> getExhibit(
            @PathVariable Long id,
            @LoginUser Long userId,
            @ParameterObject ImageResizeRequest resize
            ){
      
        ExhibitDetailResponse exhibit= exhibitService.getExhibitDetail(id, userId, resize);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibit));
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


    @Operation(summary = "전시 검색 및 필터링",description = "기간, 지역, 장르, 전시 스타일, 키워드 필터 조회 - null 시 전체선택")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_INVALID_DATE_RANGE, HomeErrorCode._HOME_UNRECOGNIZED_REGION, HomeErrorCode._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping
    public CommonResponse<CursorPaginationResponse<HomeListResponse>> getExhibit(
            @ModelAttribute ExhibitFilterRequest request,
            @ModelAttribute ImageResizeRequest resizeRequest,
            @LoginUser Long userId) {

        CursorPaginationResponse<ExhibitSearchResult> serviceResult = homeService.findExhibits(request, userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        List<HomeListResponse> data = serviceResult.getData().stream()
                .map(result -> {
                    String period = result.startDate().format(formatter) + " - " + result.endDate().format(formatter);
                    String resizedUrl = s3Service.buildResizeUrl(
                            result.posterUrl(),
                            resizeRequest.w(),
                            resizeRequest.h(),
                            resizeRequest.f()
                    );
                    return HomeListResponse.builder()
                            .exhibit_id(result.exhibitId())
                            .title(result.title())
                            .posterUrl(resizedUrl)
                            .status(result.status())
                            .exhibitPeriod(period)
                            .hallName(result.hallName())
                            .regionName(result.region())
                            .countryName(result.country())
                            .isFavorite(result.isFavorite())
                            .build();
                }).toList();

        CursorPaginationResponse<HomeListResponse> result = CursorPaginationResponse.of(
                data,
                serviceResult.isHasNext(),
                serviceResult.getNextCursor()
        );

        return CommonResponse.onSuccess(result);
    }
}
