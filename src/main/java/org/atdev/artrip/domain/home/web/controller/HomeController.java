package org.atdev.artrip.domain.home.web.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.home.reponse.HomeExhibitResponse;
import org.atdev.artrip.domain.home.service.HomeService;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping("recommend/today")
    public ResponseEntity<ApiResponse<List<HomeExhibitResponse>>> getTodayRecommendations() {
        List<HomeExhibitResponse> exhibits = homeService.getTodayRecommendedExhibits();
        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
    }


    @GetMapping("/genre")
    public ResponseEntity<List<HomeExhibitResponse>> getRandomExhibits(
            @RequestParam(defaultValue = "전체") String genre) {

        List<HomeExhibitResponse> exhibits = homeService.getThemeExhibits(genre);
        return ResponseEntity.ok(exhibits);
    }

    //    @GetMapping("/curation")
//    public ResponseEntity<ApiResponse<List<HomeExhibitResponse>>> getCuratedExhibits() {
//        List<HomeExhibitResponse> exhibits = exhibitService.getCuratedExhibits();
//        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
//    }

}
