package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.service.dto.result.AdminCurationDetailResult;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;

import java.time.LocalDate;
import java.util.List;

public record AdminCurationDetailResponse(
        Long curationId,
        String title,
        RefreshCycle refreshCycle,
        LocalDate visibleFrom,
        LocalDate visibleTo,
        boolean active,
        SortType sortType,
        List<ExhibitFilterResult.ExhibitItem> exhibits
) {
    public static AdminCurationDetailResponse from(AdminCurationDetailResult result) {
        return new AdminCurationDetailResponse(
                result.curationId(),
                result.title(),
                result.refreshCycle(),
                result.visibleFrom(),
                result.visibleTo(),
                result.active(),
                result.sortType(),
                result.exhibits()
        );
    }
}
