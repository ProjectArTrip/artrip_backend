package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.controller.dto.request.*;
import org.atdev.artrip.global.s3.util.ImageUrlFormatter;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitHallRepository;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.atdev.artrip.converter.HomeConverter;
import org.atdev.artrip.controller.dto.response.FilterResponse;

import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.controller.dto.response.RegionResponse;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.atdev.artrip.service.dto.result.GenreResult;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    private List<ExhibitRandomResult> setFavorites(List<ExhibitRandomResult> results, Set<Long> favoriteIds) {
        return results.stream()
                .map(r -> r.withFavorite(favoriteIds.contains(r.exhibitId())))
                .toList();
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
    public List<ExhibitRandomResult> getRandomPersonalized(ExhibitRandomCommand query){

        if (!userRepository.existsById(query.userId())) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        List<Keyword> userKeywords = userkeywordRepository.findByUser_UserId(query.userId())
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

        ExhibitRandomCommand command = query.withKeywords(genres, styles);

        return processExhibits(command);
    }

    // 이번주 랜덤 전시 추천
    public List<ExhibitRandomResult> getRandomSchedule(ExhibitRandomCommand query){

        ExhibitRandomCommand command = query.withLimit(2);
        return processExhibits(command);
    }


    // 장르별 전시 랜덤 추천
    public List<ExhibitRandomResult> getRandomGenre(ExhibitRandomCommand query){

        ExhibitRandomCommand command = query.withGenre();

        return processExhibits(command);
    }

    // 오늘날 전시 랜덤 추천
    public List<ExhibitRandomResult> getRandomToday(ExhibitRandomCommand query){

        ExhibitRandomCommand command = query.withLimit(3);
        return processExhibits(command);
    }

    private List<ExhibitRandomResult> processExhibits(ExhibitRandomCommand command) {

        List<ExhibitRandomResult> results = exhibitRepository.findRandomExhibits(command);

        if (command.width() != null && command.height() != null) {
            results = imageUrlFormatter.resizePosterUrls(results, command.width(), command.height(), command.format());
        }

        if (command.userId() != null) {
            Set<Long> favoriteIds = getFavoriteIds(command.userId());
            results = setFavorites(results, favoriteIds);
        }

        return results;
    }


}