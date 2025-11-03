package org.atdev.artrip.domain.home.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibitHall.repository.ExhibitHallRepository;
import org.atdev.artrip.domain.home.response.FilterResponse;
import org.atdev.artrip.domain.home.response.HomeExhibitResponse;

import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final ExhibitRepository exhibitRepository;
    private final UserKeywordRepository userkeywordRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ExhibitHallRepository exhibitHallRepository;


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

        return exhibitRepository.findThemeExhibits(genre, 2, isDomestic)
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

    public List<HomeListResponse> getPersonalized(Long userId,Boolean isDomestic){

        List<Keyword> userKeywords = userkeywordRepository.findByUser_UserId(userId)
                .stream()
                .map(UserKeyword::getKeyword)
                .toList();

        Set<String> genres = userKeywords.stream()
                .filter(k -> k.getType() == KeywordType.GENRE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        Set<String> styles = userKeywords.stream()
                .filter(k -> k.getType() == KeywordType.STYLE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        return exhibitRepository.findRandomByKeywords(genres,styles,3, isDomestic)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

    public List<HomeListResponse> getAllPersonalized(Long userId,Boolean isDomestic){

        List<Keyword> userKeywords = userkeywordRepository.findByUser_UserId(userId)
                .stream()
                .map(UserKeyword::getKeyword)
                .toList();

        Set<String> genres = userKeywords.stream()
                .filter(k -> k.getType() == KeywordType.GENRE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        Set<String> styles = userKeywords.stream()
                .filter(k -> k.getType() == KeywordType.STYLE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        return exhibitRepository.findAllByKeywords(genres,styles,isDomestic)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

    public List<HomeListResponse> getSchedule(Boolean isDomestic,LocalDate date){

        return exhibitRepository.findRandomExhibitsByDate(isDomestic,date,2)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

    public List<HomeListResponse> getAllSchedule(Boolean isDomestic,LocalDate date){

        return exhibitRepository.findAllByDate(isDomestic,date)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

    public List<String> getOverseas(){
        return exhibitHallRepository.findAllOverseasCountries();
    }

    public List<String> getDomestic(){
        return exhibitHallRepository.findAllDomesticRegions();
    }

    public List<HomeListResponse> getRandomOverseas(String country, int limit){

        return exhibitRepository.findRandomByCountry(country,limit)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

    public List<HomeListResponse> getRandomDomestic(String region, Pageable pageable){

        return exhibitRepository.findAllByRegion(region, pageable)
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();
    }

//    @Cacheable(value = "exhibit:countryPeriod",
//            key = "#country + ':' + #startDate + '-' + #endDate")
//    public List<FilterResponse> getOverSeasCondition(String country, LocalDate startDate, LocalDate endDate, Pageable page){
//        return fetchOverSeasCondition(country, startDate, endDate, page);
//    }
//
//    private List<FilterResponse> fetchOverSeasCondition(String country, LocalDate startDate, LocalDate endDate, Pageable page){
//        return exhibitRepository.findByCountryAndPeriod(country, startDate, endDate, page)
//                .stream()
//                .map(this::toFilterListResponse)
//                .toList();
//    }

    public List<FilterResponse> getFilteredExhibits(String country, LocalDate startDate, LocalDate endDate, Set<String> genres, Set<String> styles, Pageable pageable) {

        Page<Exhibit> exhibits = exhibitRepository.findExhibitsByDynamicFilters(
                country,
                startDate,
                endDate,
                genres != null && !genres.isEmpty() ? genres : null,
                styles != null && !styles.isEmpty() ? styles : null,
                pageable
        );

        return exhibits.stream()
                .map(this::toFilterListResponse)
                .toList();
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

    private FilterResponse toFilterListResponse(Exhibit exhibit){

        String period = exhibit.getStartDate().format(formatter) + " ~ " + exhibit.getEndDate().format(formatter);

        String genre = exhibit.getKeywords().stream()
                .filter(k -> k.getType() == KeywordType.GENRE)
                .map(Keyword::getName)
                .findFirst()        // 하나만 가져오기, 여러개면 List로 변경 가능
                .orElse(null);

        String style = exhibit.getKeywords().stream()
                .filter(k -> k.getType() == KeywordType.STYLE)
                .map(Keyword::getName)
                .findFirst()
                .orElse(null);

        return FilterResponse.builder()
                .exhibit_id(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .status(exhibit.getStatus())
                .exhibitPeriod(period)
                .genre(genre)
                .style(style)
                .build();
    }



}