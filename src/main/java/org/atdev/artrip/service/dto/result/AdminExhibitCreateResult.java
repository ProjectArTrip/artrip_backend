package org.atdev.artrip.service.dto.result;

public record AdminExhibitCreateResult(
        Long exhibitId
) {
    public static AdminExhibitCreateResult of(Long exhibitId) {
        return new AdminExhibitCreateResult(exhibitId);
    }
}
