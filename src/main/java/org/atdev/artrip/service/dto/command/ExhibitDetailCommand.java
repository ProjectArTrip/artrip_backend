package org.atdev.artrip.service.dto.command;

public record ExhibitDetailCommand(
        Long exhibitId,
        Long userId
) {
    public static ExhibitDetailCommand of(Long id, Long userId) {
        return new ExhibitDetailCommand(id, userId);
    }
}