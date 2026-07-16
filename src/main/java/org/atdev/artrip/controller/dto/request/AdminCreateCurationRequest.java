package org.atdev.artrip.controller.dto.request;

import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.service.dto.command.AdminCurationCreateCommand;

import java.time.LocalDate;
import java.util.List;

public record AdminCreateCurationRequest(
        String title,
        RefreshCycle refreshCycle,
        LocalDate visibleFrom,
        LocalDate visibleTo,
        boolean active,
        SortType sortType,
        List<Long> exhibitIds
) {

    public AdminCurationCreateCommand toCommand(Long adminId) {
        return AdminCurationCreateCommand.of(
                adminId,
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
