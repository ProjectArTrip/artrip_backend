package org.atdev.artrip.domain.favortie.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.favortie.dto.CalenderResponse;
import org.atdev.artrip.domain.favortie.dto.FavoriteResponse;
import org.atdev.artrip.domain.favortie.service.FavoriteExhibitService;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("favorites")
@Tag(name = "Favorite - Exhibit", description = "전시 즐겨찾기 API")
public class FavoriteController {

    private final FavoriteExhibitService favoriteExhibitService;

    @Operation(summary = "즐겨찾기 추가", description = "전시 즐겨찾기 추가")
    @PostMapping("/{exhibitId}")
    public ApiResponse<FavoriteResponse> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long exhibitId) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("Adding favorite for userId: {} , exhibit {}", userId, exhibitId);

        FavoriteResponse response = favoriteExhibitService.addFavorite(userId, exhibitId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기에서 전시를 삭제")
    @DeleteMapping("/{exhibitId}")
    public ApiResponse<Void> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long exhibitId) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("Removing favorite for userId: {} , exhibit {}", userId, exhibitId);

        favoriteExhibitService.removeFavorite(userId, exhibitId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "즐겨찾기 전체 목록 조회", description = "사용자의 모든 즐겨찾기를 조회")
    @GetMapping
    public ApiResponse<List<FavoriteResponse>> getAllFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("Getting all favorites for userId: {}", userId);

        List<FavoriteResponse> favorites = favoriteExhibitService.getAllFavorites(userId);

        return ApiResponse.onSuccess(favorites);
    }

    @Operation(
            summary = "날짜별 즐겨찾기 조회",
            description = "특정 날짜에 진행 중인 즐겨찾기 전시 조회 (캘린더, 전체 탭)")
    @GetMapping("/date")
    public ApiResponse<List<FavoriteResponse>> getFavoritesByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("data")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("Getting all favorites for userId: {}, date: {}", userId, date);

        List<FavoriteResponse> favorites = favoriteExhibitService.getFavoritesByDate(userId, date);
        return ApiResponse.onSuccess(favorites);
    }

    @Operation(
            summary = "국가별 즐겨찾기 조회",
            description = "특정 국가의 즐겨찾기 전시를 조회 (캘린더, 국가별 탭)")
    @GetMapping("/country")
    public ApiResponse<List<FavoriteResponse>> getFavoritesByCountry(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String country) {
        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("Getting all favorites for userId: {}, country: {}", userId, country);

        List<FavoriteResponse> favorites = favoriteExhibitService.getFavoritesByCountry(userId, country);
        return ApiResponse.onSuccess(favorites);
    }

    @Operation(
            summary = "캘린더 날짜 목록 조회",
            description = "특정 월에 즐겨찾기한 전시가 있는 날짜 목록 조회")
    @GetMapping("/calendar")
    public ApiResponse<CalenderResponse> getCalenderDates(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("Getting calendar dates for userId: {}, year: {}, month: {}", userId, year, month);

        CalenderResponse response = favoriteExhibitService.getCalenderDates(userId, year, month);
        return ApiResponse.onSuccess(response);
    }

    @Operation(
            summary = "즐겨찾기 국가 목록 조회",
            description = "즐겨찾기한 전시들 국가 목록 조회")
    @GetMapping("/countries")
    public ApiResponse<List<String>> getFavoriteCountries(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("Getting favorite countries for userId: {}", userId);

        List<String> countries = favoriteExhibitService.getFavoriteCountries(userId);
        return ApiResponse.onSuccess(countries);
    }

    @Operation(
            summary = "즐겨찾기 여부 확인",
            description = "특정 전시가 즐겨찾기에 추가되어 있는지 확인")
    @GetMapping("/check/{exhibitId}")
    public ApiResponse<Map<String, Boolean>> checkFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long exhibitId) {
        Long userId = Long.parseLong(userDetails.getUsername());

        boolean isFavorite = favoriteExhibitService.isFavorite(userId, exhibitId);

        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorite", isFavorite);

        return ApiResponse.onSuccess(result);
    }

}
