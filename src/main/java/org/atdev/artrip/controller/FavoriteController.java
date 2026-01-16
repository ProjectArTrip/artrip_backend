package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.CalenderResponse;
import org.atdev.artrip.controller.dto.response.FavoriteResponse;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.FavoriteExhibitService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.FavoriteError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("favorites")
@Tag(name = "Favorite-controller", description = "전시 즐겨찾기 API")
public class FavoriteController {

    private final FavoriteExhibitService favoriteExhibitService;

    @Operation(summary = "즐겨찾기 추가", description = "전시 즐겨찾기 추가")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            favorite = {FavoriteError._FAVORITE_NOT_FOUND, FavoriteError._FAVORITE_ALREADY_EXISTS, FavoriteError._FAVORITE_LIMIT_EXCEEDED}
    )
    @PostMapping("/{exhibitId}")
    public CommonResponse<FavoriteResponse> addFavorite(
            @LoginUser Long userId,
            @PathVariable Long exhibitId) {

        FavoriteResponse response = favoriteExhibitService.addFavorite(userId, exhibitId);
        return CommonResponse.onSuccess(response);
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기에서 전시를 삭제")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            favorite = {FavoriteError._FAVORITE_NOT_FOUND}
    )
    @DeleteMapping("/{exhibitId}")
    public CommonResponse<Void> removeFavorite(
            @LoginUser Long userId,
            @PathVariable Long exhibitId) {

        favoriteExhibitService.removeFavorite(userId, exhibitId);
        return CommonResponse.onSuccess(null);
    }

    @Operation(summary = "즐겨찾기 전체 목록 조회", description = "사용자의 모든 즐겨찾기를 조회")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED},
            favorite = {FavoriteError._FAVORITE_NOT_FOUND}
    )
    @GetMapping
    public CommonResponse<List<FavoriteResponse>> getAllFavorites(
            @LoginUser Long userId) {

        List<FavoriteResponse> favorites = favoriteExhibitService.getAllFavorites(userId);

        return CommonResponse.onSuccess(favorites);
    }

    @Operation(
            summary = "날짜별 즐겨찾기 조회",
            description = "특정 날짜에 진행 중인 즐겨찾기 전시 조회 (캘린더, 전체 탭)")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED},
            favorite = {FavoriteError._FAVORITE_NOT_FOUND}
    )
    @GetMapping("/date")
    public CommonResponse<List<FavoriteResponse>> getFavoritesByDate(
            @LoginUser Long userId,
            @RequestParam("data")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<FavoriteResponse> favorites = favoriteExhibitService.getFavoritesByDate(userId, date);
        return CommonResponse.onSuccess(favorites);
    }

    @Operation(
            summary = "국가별 즐겨찾기 조회",
            description = "특정 국가의 즐겨찾기 전시를 조회 (캘린더, 국가별 탭)")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED},
            favorite = {FavoriteError._FAVORITE_NOT_FOUND}
    )
    @GetMapping("/country")
    public CommonResponse<List<FavoriteResponse>> getFavoritesByCountry(
            @LoginUser Long userId,
            @RequestParam String country) {

        List<FavoriteResponse> favorites = favoriteExhibitService.getFavoritesByCountry(userId, country);

        return CommonResponse.onSuccess(favorites);
    }

    @Operation(
            summary = "캘린더 날짜 목록 조회",
            description = "특정 월에 즐겨찾기한 전시가 있는 날짜 목록 조회")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED, CommonError._INTERNAL_SERVER_ERROR},
            favorite = {FavoriteError._FAVORITE_NOT_FOUND}
    )
    @GetMapping("/calendar")
    public CommonResponse<CalenderResponse> getCalenderDates(
            @LoginUser Long userId,
            @RequestParam int year,
            @RequestParam int month) {

        CalenderResponse response = favoriteExhibitService.getCalenderDates(userId, year, month);
        return CommonResponse.onSuccess(response);
    }

    @Operation(
            summary = "즐겨찾기 국가 목록 조회",
            description = "즐겨찾기한 전시들 국가 목록 조회")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED, CommonError._INTERNAL_SERVER_ERROR},
            favorite = {FavoriteError._FAVORITE_NOT_FOUND}
    )
    @GetMapping("/countries")
    public CommonResponse<List<String>> getFavoriteCountries(
            @LoginUser Long userId) {

        List<String> countries = favoriteExhibitService.getFavoriteCountries(userId);
        return CommonResponse.onSuccess(countries);
    }

    @Operation(
            summary = "즐겨찾기 여부 확인",
            description = "특정 전시가 즐겨찾기에 추가되어 있는지 확인")
    @ApiErrorResponses(
            common = {CommonError._UNAUTHORIZED, CommonError._INTERNAL_SERVER_ERROR},
            favorite = {FavoriteError._FAVORITE_NOT_FOUND}
    )
    @GetMapping("/check/{exhibitId}")
    public CommonResponse<Map<String, Boolean>> checkFavorite(
            @LoginUser Long userId,
            @PathVariable Long exhibitId) {

        boolean isFavorite = favoriteExhibitService.isFavorite(userId, exhibitId);

        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorite", isFavorite);

        return CommonResponse.onSuccess(result);
    }

}
