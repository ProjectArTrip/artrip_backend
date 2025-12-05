package org.atdev.artrip.domain.home.converter;

import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.reponse.ExhibitDetailResponse;
import org.atdev.artrip.domain.home.response.FilterResponse;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class HomeConverter {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        String period = exhibit.getStartDate().format(formatter) + " ~ " + exhibit.getEndDate().format(formatter);

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
        String period = exhibit.getStartDate().format(formatter) + " ~ " + exhibit.getEndDate().format(formatter);

        return ExhibitDetailResponse.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .description(exhibit.getDescription())
                .posterUrl(exhibit.getPosterUrl())
                .ticketUrl(exhibit.getTicketUrl())
                .status(exhibit.getStatus())
                .exhibitPeriod(period)

                .hallName(hall != null ? hall.getName() : null)
                .hallAddress(hall != null ? hall.getAddress() : null)
                .hallOpeningHours(hall != null ? hall.getOpeningHours() : null)
                .hallPhone(hall != null ? hall.getPhone() : null)
                .build();
    }

}
