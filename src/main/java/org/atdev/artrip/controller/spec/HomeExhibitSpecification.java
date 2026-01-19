package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.request.PersonalizedRequest;
import org.atdev.artrip.controller.dto.response.ExhibitDetailResponse;
import org.atdev.artrip.controller.dto.response.GenreResponse;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.HomeErrorCode;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface HomeExhibitSpecification {

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


}
