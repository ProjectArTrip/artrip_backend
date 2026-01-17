package org.atdev.artrip.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.controller.dto.request.*;
import org.atdev.artrip.controller.dto.response.GenreResponse;
import org.atdev.artrip.global.s3.util.ImageUrlFormatter;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitHallRepository;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.atdev.artrip.converter.HomeConverter;
import org.atdev.artrip.controller.dto.response.FilterResponse;

import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.controller.dto.response.RegionResponse;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.service.dto.RandomQuery;
import org.atdev.artrip.service.dto.result.GenreResult;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final ExhibitRepository exhibitRepository;
    private final UserKeywordRepository userkeywordRepository;
    private final ExhibitHallRepository exhibitHallRepository;
    private final UserRepository userRepository;
    private final HomeConverter homeConverter;
    private final FavoriteExhibitRepository favoriteExhibitRepository;
    private final ImageUrlFormatter imageUrlFormatter;


    private Set<Long> getFavoriteIds(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        return favoriteExhibitRepository.findActiveExhibitIds(userId);
    }

    private void setFavorites(List<HomeListResponse> result, Set<Long> favoriteIds) {
        result.forEach(r -> r.setFavorite(favoriteIds.contains(r.getExhibit_id())));
    }

    // 장르 전체 조회
    public List<GenreResult> getAllGenres() {
        List<String> genreNames = exhibitRepository.findAllGenres();

        if (genreNames == null) {return List.of();}

        return genreNames.stream()
                .map(GenreResult::from)
                .toList();
    }

    // 해외 국가 목록 조회
    public List<String> getOverseas(){
        return exhibitHallRepository.findAllOverseasCountries();
    }

    public List<RegionResponse> getRegions() {
        return homeConverter.toResponseList();
    }


    //필터 전체 조회
    public FilterResponse getFilterExhibit(ExhibitFilterRequest dto, Long size, Long cursorId, Long userId) {

        Slice<Exhibit> slice = exhibitRepository.findExhibitByFilters(dto, size, cursorId);
        Set<Long> favoriteIds = getFavoriteIds(userId);
        return homeConverter.toFilterResponse(slice, favoriteIds);
    }

    // 사용자 맞춤 전시 랜덤 추천
    public List<HomeListResponse> getRandomPersonalized(RandomQuery query){

        if (!userRepository.existsById(query.userId())) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        List<Keyword> userKeywords = userkeywordRepository.findByUser_UserId(query.userId())
                .stream()
                .map(UserKeyword::getKeyword)
                .toList();

        return processExhibits(homeConverter.fromPersonalized(query, userKeywords), query);
    }

    // 이번주 랜덤 전시 추천
    public List<HomeListResponse> getRandomSchedule(RandomQuery query){

        return processExhibits(homeConverter.fromSchedule(query), query);
    }

    // 장르별 전시 랜덤 추천
    public List<HomeListResponse> getRandomGenre(RandomQuery query){

        return processExhibits(homeConverter.fromGenre(query), query);
    }

    // 오늘날 전시 랜덤 추천
    public List<HomeListResponse> getRandomToday(RandomQuery query){

        return processExhibits(homeConverter.fromToday(query), query);
    }

    private List<HomeListResponse> processExhibits(RandomExhibitRequest filter, RandomQuery query) {

        List<HomeListResponse> results = exhibitRepository.findRandomExhibits(filter);

        if (query.width() != null && query.height() != null) {
            imageUrlFormatter.resizePosterUrls(results, query.width(), query.height(), query.format());
        }

        if (query.userId() != null) {
            Set<Long> favoriteIds = getFavoriteIds(query.userId());
            setFavorites(results, favoriteIds);
        }

        return results;
    }


}