package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.domain.curation.Curation;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public record AdminCurationDetailResult(
        Long curationId,
        String title,
        RefreshCycle refreshCycle,
        LocalDate visibleFrom,
        LocalDate visibleTo,
        boolean active,
        SortType sortType,
        List<ExhibitFilterResult.ExhibitItem> exhibits
) {

    public static AdminCurationDetailResult from(Curation curation) {
        List<ExhibitFilterResult.ExhibitItem> exhibits = curation.getCurationExhibits().stream()
                .sorted(Comparator.comparingInt(ce -> ce.getDisplayOrder()))
                .map(ce -> ExhibitFilterResult.ExhibitItem.from(ce, Set.of()))
                .toList();

        return new AdminCurationDetailResult(
                curation.getCurationId(),
                curation.getTitle(),
                curation.getRefreshCycle(),
                curation.getVisibleFrom(),
                curation.getVisibleTo(),
                curation.isActive(),
                curation.getSortType(),
                exhibits
        );
    }
}
