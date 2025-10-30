package org.atdev.artrip.domain.home.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.home.response.HomeExhibitResponse;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.service.HomeService;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "오늘의 전시 추천", description = "전시데이터 3개 랜덤 조회, true=국내, false=국외")
    @GetMapping("recommend/today")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getTodayRecommendations(
            @RequestParam Boolean isDomestic) {
        List<HomeListResponse> exhibits = homeService.getTodayRecommendedExhibits(isDomestic);
        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
    }

    @Operation(summary = "장르 조회", description = "키워드 장르 데이터 전체 조회")
    @GetMapping("/genre")
    public ResponseEntity<ApiResponse<List<String>>> getGenres(){
        List<String> genres = homeService.getAllGenres();
        return ResponseEntity.ok(ApiResponse.onSuccess(genres));
    }

    @Operation(summary = "장르별 랜덤 조회", description = "true=국내, false=국외")
    @GetMapping("/genre/random")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getRandomExhibits(
            @RequestParam String genre,
            @RequestParam Boolean isDomestic){

        List<HomeListResponse> exhibits = homeService.getThemeExhibits(genre,isDomestic);
        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
    }

    @Operation(summary = "장르별 전체 조회", description = "true=국내, false=국외")
    @GetMapping("/genre/all")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getAllExhibits(
            @RequestParam String genre,
            @RequestParam Boolean isDomestic){

        List<HomeListResponse> exhibits = homeService.getAllgenreExhibits(genre,isDomestic);
        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
    }

    @Operation(summary = "장르 상세 조회")
    @GetMapping("/genre/{id}")
    public ResponseEntity<ApiResponse<HomeExhibitResponse>> getExhibit(
            @PathVariable Long id){

        HomeExhibitResponse exhibit= homeService.getExhibitDetail(id);

        return ResponseEntity.ok(ApiResponse.onSuccess(exhibit));
    }

    @Operation(summary = "사용자 맞춤 전시 추천")
    @GetMapping("/personalized")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getPersonalized(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Boolean isDomestic){//문자열 형태로 userid뽑아올수있음

        long userId = Long.parseLong(userDetails.getUsername());

        List<HomeListResponse> exhibits= homeService.getPersonalized(userId,isDomestic);

        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
    }

    @Operation(summary = "사용자 맞춤 전시 전체 조회")
    @GetMapping("/personalized/all")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getAllPersonalized(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Boolean isDomestic){

        long userId = Long.parseLong(userDetails.getUsername());

        List<HomeListResponse> exhibits= homeService.getAllPersonalized(userId,isDomestic);

        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
    }

    @Operation(summary = "이번주 전시 일정 랜덤 조회")
    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getSchedule(
            @RequestParam Boolean isDomestic,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<HomeListResponse> exhibits= homeService.getSchedule(isDomestic,date);

        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
    }

    @Operation(summary = "이번주 전시 일정 전체 조회")
    @GetMapping("/schedule/all")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getAllSchedule(
            @RequestParam(name = "isDomestic", required = false) Boolean isDomestic,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<HomeListResponse> exhibits= homeService.getAllSchedule(isDomestic,date);

        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
    }


    //    @GetMapping("/curation")
//    public ResponseEntity<ApiResponse<List<HomeExhibitResponse>>> getCuratedExhibits() {
//        List<HomeExhibitResponse> exhibits = exhibitService.getCuratedExhibits();
//        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
//    }

    @Operation(summary = "해외 국가 목록 조회")
    @GetMapping("/overseas")
    public ResponseEntity<ApiResponse<List<String>>> getOverseas(){

        List<String> OverseasList = homeService.getOverseas();

        return ResponseEntity.ok(ApiResponse.onSuccess(OverseasList));
    }

    @Operation(summary = "국내 지역 목록 조회")
    @GetMapping("/domestic")
    public ResponseEntity<ApiResponse<List<String>>> getDomestic(){

        List<String> domesticList = homeService.getDomestic();

        return ResponseEntity.ok(ApiResponse.onSuccess(domesticList));
    }

    @Operation(summary = "해외 특정 국가 랜덤 조회",description = "특정 해외 국가 전시데이터 3개 랜덤조회")
    @GetMapping("/overseas/random")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getRandomOverseas(@RequestParam String country){

        List<HomeListResponse> random = homeService.getRandomOverseas(country, 3);

        return ResponseEntity.ok(ApiResponse.onSuccess(random));
    }

    @Operation(summary = "국내 지역 전체 조회",description = "국내 지역 전시 전체 조회 1p 당 20개씩 조회.")
    @GetMapping("/domestic/all")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getRandomDomestic(@RequestParam String region){

        List<HomeListResponse> random = homeService.getRandomDomestic(region, Pageable.ofSize(20));

        return ResponseEntity.ok(ApiResponse.onSuccess(random));
    }

    @Operation(summary = "해외 전시 조건 설정",description = "특정 해외 국가 + 전시 기간으로 필터링하여 전시 데이터 조회")
    @GetMapping("/overseas/filter")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> getOverSeasCondition(@RequestParam String country,
                                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){

        List<HomeListResponse> filter = homeService.getOverSeasCondition(country,startDate,endDate,Pageable.ofSize(20));

        return ResponseEntity.ok(ApiResponse.onSuccess(filter));
    }

}
