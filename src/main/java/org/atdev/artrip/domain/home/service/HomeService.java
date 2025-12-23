package org.atdev.artrip.domain.home.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.web.dto.reponse.ExhibitDetailResponse;
import org.atdev.artrip.domain.exhibit.web.dto.request.ExhibitFilterRequestDto;
import org.atdev.artrip.domain.favortie.repository.FavoriteExhibitRepository;
import org.atdev.artrip.domain.home.web.dto.*;
import org.atdev.artrip.domain.exhibitHall.repository.ExhibitHallRepository;
import org.atdev.artrip.domain.home.converter.HomeConverter;
import org.atdev.artrip.domain.home.response.FilterResponse;

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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
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
    private final FavoriteExhibitRepository favoriteExhibitRepository;

    private Set<Long> getFavoriteIds(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        return favoriteExhibitRepository.findExhibitIdsByUserId(userId);
    }



//   //  큐레이션 전시
//    public List<HomeExhibitResponse> getCuratedExhibits() {
//        return exhibitRepository.findCuratedExhibits()
//                .stream()
//                .map(this::toHomeExhibitResponse)
//                .toList();
//    }


    public List<String> getAllGenres() {
        return exhibitRepository.findAllGenres();
    }

    public List<HomeListResponse> getAllgenreExhibits(String genre,Boolean isDomestic, Long userId){
        Set<Long> favoritesIds = getFavoriteIds(userId);

        return exhibitRepository.findAllByGenreAndDomestic(genre, isDomestic)
                .stream()
                .map(exhibit -> homeConverter.toHomeExhibitListResponse(exhibit, favoritesIds.contains(exhibit.getExhibitId())))
                .toList();
    }

    public ExhibitDetailResponse getExhibitDetail(Long exhibitId, Long userId) {

        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        boolean isFavorite = false;
        if (userId != null) {
            isFavorite = favoriteExhibitRepository.existsByUser_UserIdAndExhibit_ExhibitId(userId, exhibitId);
        }

        return homeConverter.toHomeExhibitResponse(exhibit, isFavorite);
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

        Set<Long> favoritesIds = getFavoriteIds(userId);
        return exhibitRepository.findAllByKeywords(genres,styles,isDomestic)
                .stream()
                .map(exhibit -> homeConverter.toHomeExhibitListResponse(exhibit, favoritesIds.contains(exhibit.getExhibitId())))
                .toList();
    }

    public List<HomeListResponse> getAllSchedule(Boolean isDomestic,LocalDate date, Long userId) {
        Set<Long> favoritesIds = getFavoriteIds(userId);

        return exhibitRepository.findAllByDate(isDomestic,date)
                .stream()
                .map(exhibit -> homeConverter.toHomeExhibitListResponse(exhibit, favoritesIds.contains(exhibit.getExhibitId())))
                .toList();
    }

    public List<String> getOverseas(){
        return exhibitHallRepository.findAllOverseasCountries();
    }

    public List<String> getDomestic(){
        return exhibitHallRepository.findAllDomesticRegions();
    }

    public List<HomeListResponse> getRandomOverseas(String country, int limit, Long userId) {
        Set<Long> favoritesIds = getFavoriteIds(userId);
        return exhibitRepository.findRandomByCountry(country,limit)
                .stream()
                .map(exhibit -> homeConverter.toHomeExhibitListResponse(exhibit, favoritesIds.contains(exhibit.getExhibitId())))
                .toList();
    }

    public List<HomeListResponse> getRandomDomestic(String region, Pageable pageable, Long userId) {
        Set<Long> favoritesIds = getFavoriteIds(userId);

        return exhibitRepository.findAllByRegion(region, pageable)
                .stream()
                .map(exhibit -> homeConverter.toHomeExhibitListResponse(exhibit, favoritesIds.contains(exhibit.getExhibitId())))
                .toList();
    }

    public FilterResponse getFilterExhibit(ExhibitFilterRequestDto dto, Pageable pageable, Long cursorId, Long userId) {

        Slice<Exhibit> slice = exhibitRepository.findExhibitByFilters(dto, pageable, cursorId);
        Set<Long> favoritesIds = getFavoriteIds(userId);

        return homeConverter.toFilterResponse(slice, favoritesIds);
    }

    @Transactional
    public List<HomeListResponse> getRandomPersonalized(Long userId, PersonalizedRequestDto requestDto){

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }

        List<Keyword> userKeywords = userkeywordRepository.findByUser_UserId(userId)
                .stream()
                .map(UserKeyword::getKeyword)
                .toList();

        RandomExhibitRequest filter = homeConverter.from(
                requestDto,
                userKeywords
        );

        return exhibitRepository.findRandomExhibits(filter);
    }

    public List<HomeListResponse> getRandomSchedule(ScheduleRandomRequestDto request){

        RandomExhibitRequest filter = homeConverter.from(request);

        return exhibitRepository.findRandomExhibits(filter);
    }

    public List<HomeListResponse> getRandomGenre(GenreRandomRequestDto request){

        RandomExhibitRequest filter = homeConverter.fromGenre(request);

        return exhibitRepository.findRandomExhibits(filter);
    }

    public List<HomeListResponse> getToday(TodayRandomRequestDto request){

        RandomExhibitRequest filter = homeConverter.fromToday(request);

        return exhibitRepository.findRandomExhibits(filter);
    }
}