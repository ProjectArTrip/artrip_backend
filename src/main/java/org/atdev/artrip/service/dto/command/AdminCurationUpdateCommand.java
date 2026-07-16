package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;

import java.time.LocalDate;
import java.util.List;

public record AdminCurationUpdateCommand(
        Long adminId,
        Long curationId,
        String title,
        RefreshCycle refreshCycle,
        LocalDate visibleFrom,
        LocalDate visibleTo,
        boolean active,
        SortType sortType,
        List<Long> exhibitIds
) {
    public static AdminCurationUpdateCommand of(Long adminId, Long curationId, String title, RefreshCycle refreshCycle,
                                                LocalDate visibleFrom, LocalDate visibleTo, boolean active, SortType sortType, List<Long> exhibitIds
    ) {
        return new AdminCurationUpdateCommand(
                adminId,
                curationId,
                title,
                refreshCycle,
                visibleFrom,
                visibleTo,
                active,
                sortType == null ? SortType.NONE : sortType,
                exhibitIds == null ? List.of() : exhibitIds
        );
    }
}
