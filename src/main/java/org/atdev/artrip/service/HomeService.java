package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.global.s3.util.ImageUrlFormatter;
import org.atdev.artrip.repository.*;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import org.atdev.artrip.service.dto.command.ExhibitSearchCondition;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.command.SearchHistoryCommand;
import org.atdev.artrip.service.dto.result.*;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final ExhibitRepository exhibitRepository;
    private final UserKeywordRepository userkeywordRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final FavoriteExhibitRepository favoriteExhibitRepository;
    private final ImageUrlFormatter imageUrlFormatter;
    private final SearchHistoryService searchHistoryService;


    public List<GenreResult> getAllGenres() {
        List<String> genreNames = exhibitRepository.findAllGenres();

        if (genreNames == null) {return List.of();}

        return genreNames.stream()
                .map(GenreResult::from)
                .toList();
    }

    public List<CountryResult> getOverseas() {
        return CountryResult.from();
    }

    public List<RegionResult> getRegions() {

        return regionRepository.findAll().stream()
                .map(RegionResult::from)
                .toList();
    }

    public ExhibitFilterResult searchExhibit(ExhibitSearchCondition command) {

        Slice<Exhibit> slice = exhibitRepository.findExhibitByFilters(command);
        Set<Long> favoriteIds = getFavoriteIds(command.userId());

        if (StringUtils.hasText(command.query())) {
            SearchHistoryCommand searchHistoryCommand = SearchHistoryCommand.create(command.userId(), command.query());
            searchHistoryService.saveSearchHistory(searchHistoryCommand);
        }

        return ExhibitFilterResult.of(slice,favoriteIds);
    }


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

    public List<ExhibitRandomResult> getRandomSchedule(ExhibitRandomCommand query){

        ExhibitRandomCommand command = query.withLimit(2);
        return processExhibits(command);
    }


    public List<ExhibitRandomResult> getRandomGenre(ExhibitRandomCommand query){

        ExhibitRandomCommand command = query.withGenre();

        return processExhibits(command);
    }

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
}