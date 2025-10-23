package org.atdev.artrip.domain.home.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.home.response.HomeExhibitResponse;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.service.HomeService;
import org.atdev.artrip.global.apipayload.ApiResponse;
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
    public ResponseEntity<List<String>> getGenres(){
        List<String> genres = homeService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @Operation(summary = "장르별 랜덤 조회", description = "true=국내, false=국외")
    @GetMapping("/genre/random")
    public ResponseEntity<List<HomeListResponse>> getRandomExhibits(
            @RequestParam String genre,
            @RequestParam Boolean isDomestic){

        List<HomeListResponse> exhibits = homeService.getThemeExhibits(genre,isDomestic);
        return ResponseEntity.ok(exhibits);
    }

    @Operation(summary = "장르별 전체 조회", description = "true=국내, false=국외")
    @GetMapping("/genre/all")
    public ResponseEntity<List<HomeListResponse>> getAllExhibits(
            @RequestParam String genre,
            @RequestParam Boolean isDomestic){

        List<HomeListResponse> exhibits = homeService.getAllgenreExhibits(genre,isDomestic);
        return ResponseEntity.ok(exhibits);
    }

    @Operation(summary = "장르 상세 조회")
    @GetMapping("/genre/{id}")
    public ResponseEntity<HomeExhibitResponse> getExhibit(
            @PathVariable Long id){

        HomeExhibitResponse exhibit= homeService.getExhibitDetail(id);

        return ResponseEntity.ok(exhibit);
    }

    @Operation(summary = "사용자 맞춤 전시 추천")
    @GetMapping("/personalized")
    public ResponseEntity<List<HomeListResponse>> getPersonalized(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Boolean isDomestic){//문자열 형태로 userid뽑아올수있음

        long userId = Long.parseLong(userDetails.getUsername());

        List<HomeListResponse> exhibits= homeService.getPersonalized(userId,isDomestic);

        return ResponseEntity.ok(exhibits);
    }

    @Operation(summary = "사용자 맞춤 전시 전체 조회")
    @GetMapping("/personalized/all")
    public ResponseEntity<List<HomeListResponse>> getAllPersonalized(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Boolean isDomestic){

        long userId = Long.parseLong(userDetails.getUsername());

        List<HomeListResponse> exhibits= homeService.getAllPersonalized(userId,isDomestic);

        return ResponseEntity.ok(exhibits);
    }

    @Operation(summary = "이번주 전시 일정 랜덤 조회")
    @GetMapping("/schedule")
    public ResponseEntity<List<HomeListResponse>> getSchedule(
            @RequestParam Boolean isDomestic,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<HomeListResponse> exhibits= homeService.getSchedule(isDomestic,date);

        return ResponseEntity.ok(exhibits);
    }




    //    @GetMapping("/curation")
//    public ResponseEntity<ApiResponse<List<HomeExhibitResponse>>> getCuratedExhibits() {
//        List<HomeExhibitResponse> exhibits = exhibitService.getCuratedExhibits();
//        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
//    }


}
