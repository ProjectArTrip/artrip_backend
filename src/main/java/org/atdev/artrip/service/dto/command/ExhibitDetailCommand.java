package org.atdev.artrip.service.dto.command;

public record ExhibitDetailCommand(
        Long exhibitId,
        Long userId,
        Integer width,
        Integer height,
        String format
) {
    public static ExhibitDetailCommand of(Long id, Long userId, Integer w, Integer h, String f) {
        return new ExhibitDetailCommand(id, userId, w, h, f);
    }
}