package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.controller.spec.HomeSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.HomeService;
import org.atdev.artrip.controller.dto.request.GenreRandomRequest;
import org.atdev.artrip.controller.dto.request.PersonalizedRequest;
import org.atdev.artrip.controller.dto.request.ScheduleRandomRequest;
import org.atdev.artrip.controller.dto.request.TodayRandomRequest;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController implements HomeSpecification {

    private final HomeService homeService;

    @Override
    @GetMapping("/exhibits/personalized")
    public ResponseEntity<List<HomeListResponse>> getRandomPersonalized(
            @LoginUser Long userId,
            @Valid @ModelAttribute PersonalizedRequest request){

        ExhibitRandomCommand query = ExhibitRandomCommand.builder()
                .userId(userId)
                .isDomestic(request.getIsDomestic())
                .region(request.getRegion())
                .country(request.getCountry())
                .build();

        List<ExhibitRandomResult> exhibits= homeService.getRandomPersonalized(query);


        return ResponseEntity.ok(HomeListResponse.fromList(exhibits));
    }


    @Override
    @GetMapping("/exhibits/schedule")
    public ResponseEntity<List<HomeListResponse>> getRandomSchedule(
            @Valid @ModelAttribute ScheduleRandomRequest request,
            @LoginUser Long userId){

        ExhibitRandomCommand query = ExhibitRandomCommand.builder()
                .userId(userId)
                .isDomestic(request.getIsDomestic())
                .region(request.getRegion())
                .country(request.getCountry())
                .date(request.getDate())
                .build();

        List<ExhibitRandomResult> exhibits= homeService.getRandomSchedule(query);

        return ResponseEntity.ok(HomeListResponse.fromList(exhibits));
    }


    @GetMapping("/exhibits/genres")
    public ResponseEntity<List<HomeListResponse>> getRandomExhibits(
            @Valid @ModelAttribute GenreRandomRequest request,
            @LoginUser Long userId){

        ExhibitRandomCommand query = ExhibitRandomCommand.builder()
                .userId(userId)
                .isDomestic(request.getIsDomestic())
                .region(request.getRegion())
                .country(request.getCountry())
                .singleGenre(request.getSingleGenre())
                .build();

        List<ExhibitRandomResult> exhibits = homeService.getRandomGenre(query);

        return ResponseEntity.ok(HomeListResponse.fromList(exhibits));
    }

    @Override
    @GetMapping("/exhibits/today")
    public ResponseEntity<List<HomeListResponse>> getTodayRecommendations(
            @Valid @ModelAttribute TodayRandomRequest request,
            @LoginUser Long userId){

        ExhibitRandomCommand query = ExhibitRandomCommand.builder()
                .userId(userId)
                .isDomestic(request.getIsDomestic())
                .region(request.getRegion())
                .country(request.getCountry())
                .build();
        List<ExhibitRandomResult> exhibits = homeService.getRandomToday(query);

        return ResponseEntity.ok(HomeListResponse.fromList(exhibits));
    }

}
