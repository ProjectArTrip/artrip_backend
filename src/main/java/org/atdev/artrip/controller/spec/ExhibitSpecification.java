package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.HomeErrorCode;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ExhibitSpecification {

    @Operation(summary = "전시 상세 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_EXHIBIT_NOT_FOUND}
    )
    ResponseEntity<ExhibitDetailResponse> getExhibit( @PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails userDetails,
                                                      @ParameterObject ImageResizeRequest resize);

    @Operation(summary = "장르 조회", description = "키워드 장르 데이터 전체 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_GENRE_NOT_FOUND}
    )
    ResponseEntity<List<GenreResponse>> getGenres();

    @Operation(summary = "해외 국가 목록 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED}
    )
    public ResponseEntity<List<CountryResponse>> getOverseas();

    @Operation(summary = "국내 지역 목록 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED}
    )
    public ResponseEntity<List<RegionResponse>> getDomestic();

    @Operation(summary = "전시 조건 필터 전체 조회",description = "기간, 지역, 장르, 전시 스타일 필터 조회 - null 시 전체선택")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_INVALID_DATE_RANGE, HomeErrorCode._HOME_UNRECOGNIZED_REGION, HomeErrorCode._HOME_EXHIBIT_NOT_FOUND}
    )
    public ResponseEntity<FilterResponse> getDomesticFilter(@ModelAttribute ExhibitFilterRequest dto,
                                                            @RequestParam(required = false) Long cursor,
                                                            @RequestParam(defaultValue = "20") Long size,
                                                            @AuthenticationPrincipal UserDetails userDetails,
                                                            @ParameterObject ImageResizeRequest resize);

}
