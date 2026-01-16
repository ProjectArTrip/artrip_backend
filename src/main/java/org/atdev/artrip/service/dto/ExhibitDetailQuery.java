package org.atdev.artrip.service.dto;

public record ExhibitDetailQuery(
        Long exhibitId,
        Long userId,
        Integer width,
        Integer height,
        String format
) {

    public static ExhibitDetailQuery of(Long id, Long userId, Integer w, Integer h, String f) {
        return new ExhibitDetailQuery(id, userId, w, h, f);
    }
}