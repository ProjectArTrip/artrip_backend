package org.atdev.artrip.converter;

import org.atdev.artrip.constants.DomesticRegion;
import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.controller.dto.request.*;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.controller.dto.response.ExhibitRecentResponse;
import org.atdev.artrip.controller.dto.response.FilterResponse;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.controller.dto.response.RegionResponse;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.service.dto.RandomQuery;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class HomeConverter {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public FilterResponse toFilterResponse(Slice<Exhibit> slice, Set<Long> favorites) {

        List<HomeListResponse> postDtos = slice.getContent()
                .stream()
                .map(exhibit -> toHomeExhibitListResponse(exhibit, favorites.contains(exhibit.getExhibitId())))
                .toList();

        Long nextCursor = slice.hasNext() && !slice.isEmpty()
                ? slice.getContent().get(slice.getContent().size() - 1).getExhibitId()
                : null;

        return new FilterResponse(postDtos, slice.hasNext(), nextCursor);
    }


    public HomeListResponse toHomeExhibitListResponse(Exhibit exhibit, boolean isFavorite) {

        String period = exhibit.getStartDate().format(formatter) + " - " + exhibit.getEndDate().format(formatter);

        return HomeListResponse.builder()
                .exhibit_id(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .status(exhibit.getStatus())
                .exhibitPeriod(period)
                .isFavorite(isFavorite)
                .build();
    }

    public RandomExhibitRequest fromPersonalized(RandomQuery query, List<Keyword> keywords) {

        Set<String> genres = keywords.stream()
                .filter(k -> k.getType() == KeywordType.GENRE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        Set<String> styles = keywords.stream()
                .filter(k -> k.getType() == KeywordType.STYLE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        return RandomExhibitRequest.builder()
                .isDomestic(query.isDomestic())
                .country(normalize(query.country()))
                .region(normalize(query.region()))
                .genres(toNullable(genres))
                .styles(toNullable(styles))
                .limit(3)
                .build();
    }

    public RandomExhibitRequest fromSchedule(RandomQuery query) {

        return RandomExhibitRequest.builder()
                .isDomestic(query.isDomestic())
                .country(normalize(query.country()))
                .region(normalize(query.region()))
                .date(query.date())
                .limit(2)
                .build();
    }

    public RandomExhibitRequest fromToday(RandomQuery query) {
        return RandomExhibitRequest.builder()
                .isDomestic(query.isDomestic())
                .country(normalize(query.country()))
                .region(normalize(query.region()))
                .limit(3)
                .build();
    }
    public RandomExhibitRequest fromGenre(RandomQuery query) {
        return RandomExhibitRequest.builder()
                .isDomestic(query.isDomestic())
                .country(normalize(query.country()))
                .region(normalize(query.region()))
                .genres(query.singleGenre() != null ? Set.of(query.singleGenre()) : null)
                .limit(3)
                .build();
    }

    private <T> Set<T> toNullable(Set<T> value) {
        return (value == null || value.isEmpty()) ? null : value;
    }

    private String normalize(String value) {
        if (value == null) return null;
        if ("전체".equals(value)) return null;
        return value;
    }

    public List<RegionResponse> toResponseList() {
        return Arrays.stream(DomesticRegion.values())
                .map(region -> RegionResponse.builder()
                        .region(region.getRegion())
                        .imageUrl(region.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public ExhibitRecentResponse toExhibitRecentResponse(Exhibit exhibit){

        return ExhibitRecentResponse.builder()
                .exhibitId(exhibit.getExhibitId())
                .exhibitHallName(exhibit.getExhibitHall().getName())
                .title(exhibit.getTitle())
                .build();
    }


}
