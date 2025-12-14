package org.atdev.artrip.domain.home.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.home.web.dto.RandomExhibitFilterRequestDto;
import org.atdev.artrip.domain.home.web.validationgroup.ScheduleRandomGroup;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.service.HomeService;
import org.atdev.artrip.domain.home.web.dto.RandomExhibitRequest;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.HomeError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

//-------------------------------------------------------------------------------------
    @Operation(summary = "사용자 맞춤 전시 랜덤 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED}
    )
    @GetMapping("/personalized/random")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomPersonalized(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Boolean isDomestic,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String region){

        long userId = Long.parseLong(userDetails.getUsername());

        List<HomeListResponse> exhibits= homeService.getRandomPersonalized(userId,isDomestic,country,region,3);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

    @Operation(summary = "이번주 전시 일정 랜덤 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED}
    )
    @PostMapping("/schedule")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomSchedule(
            @Validated(ScheduleRandomGroup.class)
            @RequestBody RandomExhibitFilterRequestDto request){

        List<HomeListResponse> exhibits= homeService.getRandomSchedule(request);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

    @Operation(summary = "장르별 랜덤 조회", description = "true=국내, false=국외")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_GENRE_NOT_FOUND}
    )
    @GetMapping("/genre/random")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomExhibits(
            @RequestParam String genre,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String region,
            @RequestParam Boolean isDomestic){

        List<HomeListResponse> exhibits = homeService.getRandomGenre(isDomestic,country,region,genre,3);
        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

    @Operation(summary = "오늘의(국가별) 전시 추천", description = "전시데이터 3개 랜덤 조회, true=국내, false=국외")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("recommend/today")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getTodayRecommendations(
            @RequestParam Boolean isDomestic,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String region) {

        List<HomeListResponse> exhibits = homeService.getToday(isDomestic,country,region,3);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

    //    @GetMapping("/curation")
//    public ResponseEntity<ApiResponse<List<HomeExhibitResponse>>> getCuratedExhibits() {
//        List<HomeExhibitResponse> exhibits = exhibitService.getCuratedExhibits();
//        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
//    }
}
