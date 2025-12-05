package org.atdev.artrip.domain.home.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.reponse.ExhibitDetailResponse;
import org.atdev.artrip.domain.exhibit.web.dto.ExhibitFilterDto;
import org.atdev.artrip.domain.exhibitHall.repository.ExhibitHallRepository;
import org.atdev.artrip.domain.home.converter.HomeConverter;
import org.atdev.artrip.domain.home.response.FilterResponse;
import org.atdev.artrip.domain.home.response.HomeExhibitResponse;

import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.atdev.artrip.global.apipayload.code.status.ExhibitError;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final ExhibitHallRepository exhibitHallRepository;
    private final UserRepository userRepository;
    private final HomeConverter homeConverter;


    // 오늘 추천 전시
    public List<HomeListResponse> getTodayRecommendedExhibits(Boolean isDomestic) {
        return exhibitRepository.findRandomExhibits(3,isDomestic)
                .stream()
                .map(homeConverter::toHomeExhibitListResponse)
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
                .map(homeConverter::toHomeExhibitListResponse)
                .toList();
    }

    public List<String> getAllGenres() {
        return exhibitRepository.findAllGenres();
    }

    public List<HomeListResponse> getAllgenreExhibits(String genre,Boolean isDomestic){

        return exhibitRepository.findAllByGenreAndDomestic(genre, isDomestic)
                .stream()
                .map(homeConverter::toHomeExhibitListResponse)
                .toList();
    }

    public ExhibitDetailResponse getExhibitDetail(Long exhibitId) {

        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        return homeConverter.toHomeExhibitResponse(exhibit);
    }

    public List<HomeListResponse> getPersonalized(Long userId,Boolean isDomestic){

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }

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
                .map(homeConverter::toHomeExhibitListResponse)
                .toList();
    }

    public List<HomeListResponse> getAllPersonalized(Long userId,Boolean isDomestic){

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }

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
                .map(homeConverter::toHomeExhibitListResponse)
                .toList();
    }

    public List<HomeListResponse> getSchedule(Boolean isDomestic,LocalDate date){

        return exhibitRepository.findRandomExhibitsByDate(isDomestic,date,2)
                .stream()
                .map(homeConverter::toHomeExhibitListResponse)
                .toList();
    }

    public List<HomeListResponse> getAllSchedule(Boolean isDomestic,LocalDate date){

        return exhibitRepository.findAllByDate(isDomestic,date)
                .stream()
                .map(homeConverter::toHomeExhibitListResponse)
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
                .map(homeConverter::toHomeExhibitListResponse)
                .toList();
    }

    public List<HomeListResponse> getRandomDomestic(String region, Pageable pageable){

        return exhibitRepository.findAllByRegion(region, pageable)
                .stream()
                .map(homeConverter::toHomeExhibitListResponse)
                .toList();
    }

    public FilterResponse getFilterExhibit(ExhibitFilterDto dto, Pageable pageable, Long cursorId) {

        Slice<Exhibit> slice = exhibitRepository.findExhibitByFilters(dto, pageable, cursorId);

        return homeConverter.toFilterResponse(slice);
    }



}