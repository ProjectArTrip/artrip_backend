package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.controller.spec.ExhibitSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.ExhibitService;
import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.service.HomeService;
import org.atdev.artrip.service.dto.command.ExhibitSearchCondition;
import org.atdev.artrip.service.dto.result.*;
import org.atdev.artrip.service.dto.command.ExhibitDetailCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exhibits")
public class ExhibitController implements ExhibitSpecification {

    private final HomeService homeService;
    private final ExhibitService exhibitService;

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
            @LoginUser Long userId
            ){

        ExhibitDetailCommand query = ExhibitDetailCommand.of(id, userId);
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



    @GetMapping
    public ResponseEntity<FilterResponse> searchExhibit(@ModelAttribute ExhibitFilterRequest dto,
                                                        @RequestParam(required = false) Long cursor,
                                                        @RequestParam(defaultValue = "20") Long size,
                                                        @LoginUser Long userId) {
        ExhibitSearchCondition command = dto.toCommand(userId, cursor, size);

        ExhibitFilterResult result = homeService.searchExhibit(command);

        return ResponseEntity.ok(FilterResponse.from(result));
    }

}
