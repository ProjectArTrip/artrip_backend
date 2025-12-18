package org.atdev.artrip.domain.home.converter;

import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.reponse.ExhibitDetailResponse;
import org.atdev.artrip.domain.home.web.dto.*;
import org.atdev.artrip.domain.home.response.FilterResponse;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class HomeConverter {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public FilterResponse toFilterResponse(Slice<Exhibit> slice) {

        List<HomeListResponse> postDtos = slice.getContent()
                .stream()
                .map(this::toHomeExhibitListResponse)
                .toList();

        Long nextCursor = slice.hasNext()
                ? slice.getContent().get(slice.getContent().size() - 1).getExhibitId()
                : null;

        return new FilterResponse(postDtos, slice.hasNext(), nextCursor);
    }


    public HomeListResponse toHomeExhibitListResponse(Exhibit exhibit){

        String period = exhibit.getStartDate().format(formatter) + " - " + exhibit.getEndDate().format(formatter);

        return HomeListResponse.builder()
                .exhibit_id(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .status(exhibit.getStatus())
                .exhibitPeriod(period)
                .build();
    }

    public ExhibitDetailResponse toHomeExhibitResponse(Exhibit exhibit) {

        var hall = exhibit.getExhibitHall();
        String period = exhibit.getStartDate().format(formatter) + " - " + exhibit.getEndDate().format(formatter);

        Double lat = hall.getLatitude() != null ? hall.getLatitude().doubleValue() : null;
        Double lng = hall.getLongitude() != null ? hall.getLongitude().doubleValue() : null;

        return ExhibitDetailResponse.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .description(exhibit.getDescription())
                .posterUrl(exhibit.getPosterUrl())
                .ticketUrl(exhibit.getTicketUrl())
                .status(exhibit.getStatus())
                .exhibitPeriod(period)

                .hallName(hall != null ? hall.getName() : null)// exhibit과 exhibithall이 연결되어있지않아도 체크 가능
                .hallAddress(hall != null ? hall.getAddress() : null)
                .hallOpeningHours(hall != null ? hall.getOpeningHours() : null)
                .hallPhone(hall != null ? hall.getPhone() : null)
                .hallLatitude(lat)
                .hallLongitude(lng)
                .build();
    }


    public RandomExhibitRequest from(PersonalizedRequestDto request, List<Keyword> keywords) {

        Set<String> genres = keywords.stream()
                .filter(k -> k.getType() == KeywordType.GENRE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        Set<String> styles = keywords.stream()
                .filter(k -> k.getType() == KeywordType.STYLE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        return RandomExhibitRequest.builder()
                .isDomestic(request.getIsDomestic())
                .country(normalize(request.getCountry()))
                .region(normalize(request.getRegion()))
                .genres(isEmpty(genres))
                .styles(isEmpty(styles))
                .limit(3)
                .build();
    }


    private String normalize(String value) {
        if (value == null) return null;
        if ("전체".equals(value)) return null;
        return value;
    }

    public RandomExhibitRequest from(ScheduleRandomRequestDto request) {

        return RandomExhibitRequest.builder()
                .isDomestic(request.getIsDomestic())
                .country(normalize(request.getCountry()))
                .region(normalize(request.getRegion()))
                .date(request.getDate())
                .limit(2)
                .build();
    }

    public RandomExhibitRequest fromToday(TodayRandomRequestDto request) {
        return RandomExhibitRequest.builder()
                .isDomestic(request.getIsDomestic())
                .country(request.getCountry())
                .region(request.getRegion())
                .limit(3)
                .build();
    }

    private <T> Set<T> isEmpty(Set<T> value) {
        return (value == null || value.isEmpty()) ? null : value;
    }

    public RandomExhibitRequest fromGenre(GenreRandomRequestDto request) {
        return RandomExhibitRequest.builder()
                .isDomestic(request.getIsDomestic())
                .country(request.getCountry())
                .region(request.getRegion())
                .singleGenre(request.getSingleGenre())
                .limit(3)
                .build();
    }

}
