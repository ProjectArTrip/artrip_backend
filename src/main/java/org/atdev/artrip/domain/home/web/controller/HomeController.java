package org.atdev.artrip.domain.home.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.home.response.HomeExhibitResponse;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.service.HomeService;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    //    @GetMapping("/curation")
//    public ResponseEntity<ApiResponse<List<HomeExhibitResponse>>> getCuratedExhibits() {
//        List<HomeExhibitResponse> exhibits = exhibitService.getCuratedExhibits();
//        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
//    }


}
