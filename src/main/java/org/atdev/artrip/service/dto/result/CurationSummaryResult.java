package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.curation.CurationExhibit;

import java.util.List;
import java.util.Set;

public record CurationSummaryResult(
        Long curationId,
        String title,
        String subtitle,
        List<ExhibitFilterResult.ExhibitItem> exhibits
) {
    public static CurationSummaryResult of(Curation curation, List<CurationExhibit> sampled, Set<Long> favoriteExhibitIds) {
        return new CurationSummaryResult(
                curation.getCurationId(),
                curation.getTitle(),
                curation.getSubtitle(),
                sampled.stream()
                        .map(curationExhibit -> ExhibitFilterResult.ExhibitItem.from(curationExhibit, favoriteExhibitIds))
                        .toList()
        );
    }
}
