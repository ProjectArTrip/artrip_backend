package org.atdev.artrip.converter;

import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.controller.dto.response.ExhibitRecentResponse;
import org.atdev.artrip.controller.dto.response.FilterResponse;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

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
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .status(exhibit.getStatus())
                .exhibitPeriod(period)
                .isFavorite(isFavorite)
                .build();
    }




    public ExhibitRecentResponse toExhibitRecentResponse(Exhibit exhibit){

        return ExhibitRecentResponse.builder()
                .exhibitId(exhibit.getExhibitId())
                .exhibitHallName(exhibit.getExhibitHall().getName())
                .title(exhibit.getTitle())
                .build();
    }


}
