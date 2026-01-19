package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.controller.spec.ExhibitSpecification;
import org.atdev.artrip.service.ExhibitService;
import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.service.HomeService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.service.dto.command.ExhibitFilterCommand;
import org.atdev.artrip.service.dto.result.*;
import org.atdev.artrip.service.dto.command.ExhibitDetailCommand;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exhibit")
public class ExhibitController implements ExhibitSpecification {

    private final HomeService homeService;
    private final ExhibitService exhibitService;

    private Long getUserId(UserDetails userDetails) {
        return userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
    }

    @Override
    @GetMapping("/genre")
    public ResponseEntity<List<GenreResponse>> getGenres(){

        List<GenreResult> genres = homeService.getAllGenres();

        return ResponseEntity.ok(GenreResponse.from(genres));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ExhibitDetailResponse> getExhibit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ParameterObject ImageResizeRequest resize
            ){

        ExhibitDetailCommand query = ExhibitDetailCommand.of(id, getUserId(userDetails), resize.getW(), resize.getH(), resize.getF());
        ExhibitDetailResult result = exhibitService.getExhibitDetail(query);

        return ResponseEntity.ok(ExhibitDetailResponse.from(result));
    }

    @Override
    @GetMapping("/overseas")
    public ResponseEntity<List<CountryResponse>> getOverseas() {

        List<CountryResult> OverseasList = homeService.getOverseas();

        return ResponseEntity.ok(CountryResponse.from(OverseasList));
    }

    @Override
    @GetMapping("/domestic")
    public ResponseEntity<List<RegionResponse>> getDomestic(){

        List<RegionResult> results = homeService.getRegions();

        return ResponseEntity.ok(RegionResponse.from(results));
    }


    @Override
    @GetMapping("/filter")
    public ResponseEntity<FilterResponse> getDomesticFilter(@ModelAttribute ExhibitFilterRequest dto,
                                                            @RequestParam(required = false) Long cursor,
                                                            @RequestParam(defaultValue = "20") Long size,
                                                            @AuthenticationPrincipal UserDetails userDetails,
                                                            @ParameterObject ImageResizeRequest resize) {

        ExhibitFilterCommand command = ExhibitFilterCommand.builder()
                .isDomestic(dto.isDomestic())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .country(dto.country())
                .region(dto.region())
                .genres(dto.genres())
                .styles(dto.styles())
                .sortType(dto.sortType())

                .userId(getUserId(userDetails))
                .cursor(cursor)
                .size(size)

                .width(resize.getW())
                .height(resize.getH())
                .format(resize.getF())
                .build();


        ExhibitFilterResult result = homeService.getFilterExhibit(command);

        return ResponseEntity.ok(FilterResponse.from(result));
    }

}
