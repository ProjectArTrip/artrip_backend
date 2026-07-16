package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.service.dto.command.AdminCurationUpdateCommand;

import java.time.LocalDate;
import java.util.List;

public record AdminUpdateCurationRequest(
        String title,
        RefreshCycle refreshCycle,
        LocalDate visibleFrom,
        LocalDate visibleTo,
        boolean active,
        SortType sortType,
        List<Long> exhibitIds
) {
    public AdminCurationUpdateCommand toCommand(Long adminId, Long curationId) {
        return AdminCurationUpdateCommand.of(
                adminId,
                curationId,
                title,
                refreshCycle,
                visibleFrom,
                visibleTo,
                active,
                sortType,
                exhibitIds
        );
    }
}
