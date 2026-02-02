package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.atdev.artrip.controller.dto.request.*;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.controller.dto.response.HomeResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.HomeErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

public interface HomeSpecification {

    @Operation(summary = "사용자 맞춤 전시 랜덤 조회",
            description = """
    [요청 규칙]
    - isDomestic = true (국내)
      - region: 필수
      - country: 사용하지 않음
    - isDomestic = false (국외)
      - country: 필수
      - region: 사용하지 않음
    
       예시 요청:
    {
      "isDomestic": true,
      "region": "전체"
    }
    """)
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED}
    )
    public ResponseEntity<HomeListResponse> getRandomPersonalized(
            @LoginUser Long userId,
            @Valid @ModelAttribute PersonalizedRequest request);


    @Operation(
            summary = "이번주 전시 일정 랜덤 조회",
            description = """
    [요청 규칙]
    - isDomestic = true (국내)
      - region: 필수
      - country: 사용하지 않음
    - isDomestic = false (국외)
      - country: 필수
      - region: 사용하지 않음
    - date: 필수
    
      예시 요청:
      {
        "isDomestic": true,
        "region": "전체",
        "date": "2025-12-16"
      }
    """
    )
    public ResponseEntity<HomeListResponse> getRandomSchedule(
            @Valid @ModelAttribute ScheduleRandomRequest request,
            @LoginUser Long userId);

    @Operation(summary = "장르별 랜덤 조회",
            description = """
    [요청 규칙]
    - isDomestic = true (국내)
      - region: 필수
      - country: 사용하지 않음
    - isDomestic = false (국외)
      - country: 필수
      - region: 사용하지 않음
    - singleGenre: 필수
    
      예시 요청:
    {
      "isDomestic": true,
      "region": "전체",
      "singleGenre": "현대 미술"
    }
    """)
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_GENRE_NOT_FOUND}
    )
    public ResponseEntity<HomeListResponse> getRandomExhibits(
            @Valid @ModelAttribute GenreRandomRequest request,
            @LoginUser Long userId);

    @Operation(summary = "오늘의(국가/지역별) 전시 랜덤 추천",
            description = """
    [요청 규칙]
    - isDomestic = true (국내)
      - region: 필수
      - country: 사용하지 않음
    - isDomestic = false (국외)
      - country: 필수
      - region: 사용하지 않음
    
      예시 요청:
    {
      "isDomestic": true,
      "region": "전체"
    }
    """)
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST, CommonErrorCode._UNAUTHORIZED},
            home = {HomeErrorCode._HOME_EXHIBIT_NOT_FOUND}
    )
    public ResponseEntity<HomeListResponse> getTodayRecommendations(
            @Valid @ModelAttribute TodayRandomRequest request,
            @LoginUser Long userId);
}
