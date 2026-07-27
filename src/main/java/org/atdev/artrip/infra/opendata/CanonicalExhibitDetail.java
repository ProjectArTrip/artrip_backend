package org.atdev.artrip.infra.opendata;

public record CanonicalExhibitDetail(
        String description,
        String ticketUrl,
        String phone,
        String placeAddr,
        String placeHomepageUrl
) {
}
