package org.atdev.artrip.domain.home.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.reponse.ExhibitDetailResponse;
import org.atdev.artrip.domain.exhibit.web.dto.ExhibitFilterDto;
import org.atdev.artrip.domain.home.response.FilterResponse;
import org.atdev.artrip.domain.home.response.HomeExhibitResponse;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.service.HomeService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.HomeError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("recommend/today")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getTodayRecommendations(
            @RequestParam Boolean isDomestic) {
        List<HomeListResponse> exhibits = homeService.getTodayRecommendedExhibits(isDomestic);
        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

    @Operation(summary = "장르 조회", description = "키워드 장르 데이터 전체 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_GENRE_NOT_FOUND}
    )
    @GetMapping("/genre")
    public ResponseEntity<CommonResponse<List<String>>> getGenres(){
        List<String> genres = homeService.getAllGenres();
        return ResponseEntity.ok(CommonResponse.onSuccess(genres));
    }

    @Operation(summary = "장르별 전시 조회", description = "true=국내, false=국외")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_GENRE_NOT_FOUND}
    )
    @GetMapping("/genre/all")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getAllExhibits(
            @RequestParam String genre,
            @RequestParam Boolean isDomestic){

        List<HomeListResponse> exhibits = homeService.getAllgenreExhibits(genre,isDomestic);
        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

    @Operation(summary = "전시 상세 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ExhibitDetailResponse>> getExhibit(
            @PathVariable Long id){

        ExhibitDetailResponse exhibit= homeService.getExhibitDetail(id);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibit));
    }

    @Operation(summary = "사용자 맞춤 전시 전체 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("/personalized/all")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getAllPersonalized(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Boolean isDomestic){

        long userId = Long.parseLong(userDetails.getUsername());

        List<HomeListResponse> exhibits= homeService.getAllPersonalized(userId,isDomestic);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }

    @Operation(summary = "이번주 전시 일정 전체 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("/schedule/all")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getAllSchedule(
            @RequestParam(name = "isDomestic", required = false) Boolean isDomestic,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<HomeListResponse> exhibits= homeService.getAllSchedule(isDomestic,date);

        return ResponseEntity.ok(CommonResponse.onSuccess(exhibits));
    }


    //    @GetMapping("/curation")
//    public ResponseEntity<ApiResponse<List<HomeExhibitResponse>>> getCuratedExhibits() {
//        List<HomeExhibitResponse> exhibits = exhibitService.getCuratedExhibits();
//        return ResponseEntity.ok(ApiResponse.onSuccess(exhibits));
//    }

    @Operation(summary = "해외 국가 목록 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED}
    )
    @GetMapping("/overseas")
    public ResponseEntity<CommonResponse<List<String>>> getOverseas(){

        List<String> OverseasList = homeService.getOverseas();

        return ResponseEntity.ok(CommonResponse.onSuccess(OverseasList));
    }

    @Operation(summary = "국내 지역 목록 조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED}
    )
    @GetMapping("/domestic")
    public ResponseEntity<CommonResponse<List<String>>> getDomestic(){

        List<String> domesticList = homeService.getDomestic();

        return ResponseEntity.ok(CommonResponse.onSuccess(domesticList));
    }

    @Operation(summary = "해외 특정 국가 랜덤 조회",description = "특정 해외 국가 전시데이터 3개 랜덤조회")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_UNRECOGNIZED_REGION, HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("/overseas/random")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomOverseas(@RequestParam String country){

        List<HomeListResponse> random = homeService.getRandomOverseas(country, 3);

        return ResponseEntity.ok(CommonResponse.onSuccess(random));
    }

    @Operation(summary = "국내 지역 전체 조회",description = "국내 지역 전시 전체 조회 1p 당 20개씩 조회.")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_UNRECOGNIZED_REGION, HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("/domestic/all")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomDomestic(@RequestParam String region){

        List<HomeListResponse> random = homeService.getRandomDomestic(region, Pageable.ofSize(20));

        return ResponseEntity.ok(CommonResponse.onSuccess(random));
    }

    @Operation(summary = "전시 조건 필터",description = "기간, 지역, 장르, 전시 스타일 필터 조회 - null 시 전체선택")
    @ApiErrorResponses(
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_INVALID_DATE_RANGE, HomeError._HOME_UNRECOGNIZED_REGION, HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @PostMapping("/filter")
    public ResponseEntity<FilterResponse> getDomesticFilter(@RequestBody ExhibitFilterDto dto,
                                                                         @RequestParam(required = false) Long cursor,
                                                                         @PageableDefault(size = 20) Pageable pageable){

        FilterResponse exhibits = homeService.getFilterExhibit(dto, pageable, cursor);

        return ResponseEntity.ok(exhibits);

    }

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
            common = {CommonError._BAD_REQUEST, CommonError._UNAUTHORIZED},
            home = {HomeError._HOME_EXHIBIT_NOT_FOUND}
    )
    @GetMapping("/schedule")
    public ResponseEntity<CommonResponse<List<HomeListResponse>>> getRandomSchedule(
            @RequestParam Boolean isDomestic,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String region,
            @RequestParam LocalDate date){

        List<HomeListResponse> exhibits= homeService.getRandomSchedule(isDomestic,country,region, date, 3);

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
}
