package org.atdev.artrip.service.dto.result;

import java.util.List;

public record CurationSummaryResult(
        Long curationId,
        String title,
        String subtitle,
        List<ExhibitItem> exhibits
) {
    public record ExhibitItem(
            Long exhibitId,
            String posterUrl,
            String title,
            String exhibitHall,
            String location,
            String exhibitPeriod,
            boolean favorite
    ){}
    public static CurationSummaryResult of(Long curationId, String title, String subtitle,List<ExhibitItem> item) {
        return new CurationSummaryResult(curationId, title, subtitle, item);
    }
}
