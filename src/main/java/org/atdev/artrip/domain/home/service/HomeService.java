package org.atdev.artrip.domain.home.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.home.response.HomeExhibitResponse;

import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final ExhibitRepository exhibitRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    // 오늘 추천 전시
    public List<HomeExhibitResponse> getTodayRecommendedExhibits(Boolean isDomestic) {
        return exhibitRepository.findRandomExhibits(3,isDomestic)
                .stream()
                .map(this::toHomeExhibitResponse)
                .toList();
    }

//   //  큐레이션 전시
//    public List<HomeExhibitResponse> getCuratedExhibits() {
//        return exhibitRepository.findCuratedExhibits()
//                .stream()
//                .map(this::toHomeExhibitResponse)
//                .toList();
//    }

    public List<HomeExhibitResponse> getThemeExhibits(String genre,Boolean isDomestic) {

        return exhibitRepository.findThemeExhibits(genre, 3, isDomestic)
                .stream()
                .map(this::toHomeExhibitResponse)
                .toList();
    }

    public List<String> getAllGenres() {
        return exhibitRepository.findAllGenres();
    }

    private HomeExhibitResponse toHomeExhibitResponse(Exhibit exhibit) {
        var hall = exhibit.getExhibitHall();
        return HomeExhibitResponse.builder()
                .exhibit_id(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .status(exhibit.getStatus())
                .country(hall != null ? hall.getCountry() : null)
                .region(hall != null ? hall.getRegion() : null)
                .startDate(exhibit.getStartDate().format(formatter))
                .endDate(exhibit.getEndDate().format(formatter))
                .build();
    }

}