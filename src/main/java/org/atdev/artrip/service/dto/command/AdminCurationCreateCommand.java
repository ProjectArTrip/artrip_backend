package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;

import java.time.LocalDate;
import java.util.List;

public record AdminCurationCreateCommand(
        Long adminId,
        String title,
        RefreshCycle refreshCycle,
        LocalDate visibleFrom,
        LocalDate visibleTo,
        boolean active,
        SortType sortType,
        List<Long> exhibitIds
) {
    public static AdminCurationCreateCommand of(Long adminId, String title, RefreshCycle refreshCycle, LocalDate visibleFrom,
                                                LocalDate visibleTo, boolean active, SortType sortType, List<Long> exhibitIds
    ){
        return new AdminCurationCreateCommand(
                adminId,
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
