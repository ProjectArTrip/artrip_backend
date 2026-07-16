package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.AdminCurationListResult;

import java.time.LocalDate;

public record AdminCurationListResponse(
        Long curationId,
        String title,
        int exhibitCount,
        LocalDate createdAt,
        LocalDate visibleFrom,
        LocalDate visibleTo,
        boolean active
) {

    public static AdminCurationListResponse from(AdminCurationListResult result) {
        return new AdminCurationListResponse(
                result.curationId(),
                result.title(),
                result.exhibitCount(),
                result.createdAt(),
                result.visibleFrom(),
                result.visibleTo(),
                result.active()
        );
    }
}
