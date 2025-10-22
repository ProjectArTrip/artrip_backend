package org.atdev.artrip.domain.home.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.home.response.HomeExhibitResponse;

import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final ExhibitRepository exhibitRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    // 오늘 추천 전시
    public List<HomeListResponse> getTodayRecommendedExhibits(Boolean isDomestic) {
        return exhibitRepository.findRandomExhibits(3,isDomestic)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

//   //  큐레이션 전시
//    public List<HomeExhibitResponse> getCuratedExhibits() {
//        return exhibitRepository.findCuratedExhibits()
//                .stream()
//                .map(this::toHomeExhibitResponse)
//                .toList();
//    }

    public List<HomeListResponse> getThemeExhibits(String genre,Boolean isDomestic) {

        return exhibitRepository.findThemeExhibits(genre, 3, isDomestic)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

    public List<String> getAllGenres() {
        return exhibitRepository.findAllGenres();
    }

    public List<HomeListResponse> getAllgenreExhibits(String genre,Boolean isDomestic){

        return exhibitRepository.findAllByGenreAndDomestic(genre, isDomestic)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

    public HomeExhibitResponse getExhibitDetail(Long exhibitId) {
        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new RuntimeException("해당 전시를 찾을 수 없습니다"));

        return toHomeExhibitResponse(exhibit);
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

    private HomeListResponse toHomeExhibitListResponse(Exhibit exhibit){

        String period = exhibit.getStartDate().format(formatter) + " ~ " + exhibit.getEndDate().format(formatter);

        return HomeListResponse.builder()
                .exhibit_id(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .status(exhibit.getStatus())
                .exhibitPeriod(period)
                .build();
    }

}