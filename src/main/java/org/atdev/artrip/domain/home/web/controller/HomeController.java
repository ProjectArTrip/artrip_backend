package org.atdev.artrip.domain.home.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.service.HomeService;
import org.atdev.artrip.domain.home.web.dto.request.GenreRandomRequest;
import org.atdev.artrip.domain.home.web.dto.request.PersonalizedRequest;
import org.atdev.artrip.domain.home.web.dto.request.ScheduleRandomRequest;
import org.atdev.artrip.domain.home.web.dto.request.TodayRandomRequest;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.HomeError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

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
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED}
    )
    @PostMapping("/personalized/random")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomPersonalized(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PersonalizedRequest requestDto){

        long userId = Long.parseLong(userDetails.getUsername());

        List<HomeListResponse> exhibits= homeService.getRandomPersonalized(userId, requestDto);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

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
    @PostMapping("/schedule")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomSchedule(
            @Valid @RequestBody ScheduleRandomRequest request){

        List<HomeListResponse> exhibits= homeService.getRandomSchedule(request);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

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
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_GENRE_NOT_FOUND}
    )
    @PostMapping("/genre/random")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomExhibits(
            @Valid @RequestBody GenreRandomRequest request){

        List<HomeListResponse> exhibits = homeService.getRandomGenre(request);
        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

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
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @PostMapping("recommend/today")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getTodayRecommendations(
            @Valid @RequestBody TodayRandomRequest request){

        List<HomeListResponse> exhibits = homeService.getRandomToday(request);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

    //    @GetMapping("/curation")
//    public ResponseEntity<ApiResponse<List<HomeExhibitResponse>>> getCuratedExhibits() {
//        List<HomeExhibitResponse> exhibits = exhibitService.getCuratedExhibits();
//        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
//    }

}
