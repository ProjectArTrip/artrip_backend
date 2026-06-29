package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.curation.CurationExhibit;

import java.util.List;
import java.util.Set;

public record CurationSummaryResult(
        Long curationId,
        String title,
        List<ExhibitFilterResult.ExhibitItem> exhibits
) {
    public static CurationSummaryResult of(Curation curation, List<CurationExhibit> sampled, Set<Long> favoriteExhibitIds) {
        return new CurationSummaryResult(
                curation.getCurationId(),
                curation.getTitle(),
                sampled.stream()
                        .map(curationExhibit -> ExhibitFilterResult.ExhibitItem.from(curationExhibit, favoriteExhibitIds))
                        .toList()
        );
    }
}
