package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.controller.dto.response.HomeResponse;
import org.atdev.artrip.controller.spec.HomeSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.HomeService;
import org.atdev.artrip.controller.dto.request.GenreRandomRequest;
import org.atdev.artrip.controller.dto.request.PersonalizedRequest;
import org.atdev.artrip.controller.dto.request.ScheduleRandomRequest;
import org.atdev.artrip.controller.dto.request.TodayRandomRequest;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.result.ExhibitRandomListResult;
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
    public ResponseEntity<HomeListResponse> getRandomPersonalized(
            @LoginUser Long userId,
            @Valid @ModelAttribute PersonalizedRequest request){

        ExhibitRandomCommand command = request.toCommand(userId);
        ExhibitRandomListResult result= homeService.getRandomPersonalized(command);


        return ResponseEntity.ok(HomeListResponse.from(result));
    }


    @Override
    @GetMapping("/exhibits/schedule")
    public ResponseEntity<HomeListResponse> getRandomSchedule(
            @Valid @ModelAttribute ScheduleRandomRequest request,
            @LoginUser Long userId){

        ExhibitRandomCommand command = request.toCommand(userId);
        ExhibitRandomListResult result= homeService.getRandomSchedule(command);

        return ResponseEntity.ok(HomeListResponse.from(result));
    }


    @GetMapping("/exhibits/genres")
    public ResponseEntity<HomeListResponse> getRandomExhibits(
            @Valid @ModelAttribute GenreRandomRequest request,
            @LoginUser Long userId){

        ExhibitRandomCommand command = request.toCommand(userId);
        ExhibitRandomListResult result = homeService.getRandomGenre(command);

        return ResponseEntity.ok(HomeListResponse.from(result));
    }

    @Override
    @GetMapping("/exhibits/today")
    public ResponseEntity<HomeListResponse> getTodayRecommendations(
            @Valid @ModelAttribute TodayRandomRequest request,
            @LoginUser Long userId){

        ExhibitRandomCommand command = request.toCommand(userId);
        ExhibitRandomListResult result = homeService.getRandomToday(command);

        return ResponseEntity.ok(HomeListResponse.from(result));
    }

}
