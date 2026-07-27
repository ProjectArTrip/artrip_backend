package org.atdev.artrip.infra.opendata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CultureInfoResponse(Body body) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(Integer totalCount, Items items) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
            @JacksonXmlElementWrapper(useWrapping = false)
            List<Item> item
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String seq,
            String title,
            String startDate,
            String endDate,
            String place,
            String area,
            String thumbnail,
            String gpsX,
            String gpsY,
            String contents1,
            String url,
            String phone,
            String placeAddr,
            String placeUrl
    ) {
    }
}
