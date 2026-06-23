package org.atdev.artrip.domain.exhibit.event;

import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.keyword.Keyword;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record ExhibitCreatedEvent(
        List<ExhibitSummary> exhibits
) {

    public record ExhibitSummary(
            Long exhibitId,
            String title,
            String hallName,
            Set<Long> keywordIds
    ) {
        public static ExhibitSummary from(Exhibit exhibit) {
            return new ExhibitSummary(
                    exhibit.getExhibitId(),
                    exhibit.getTitle(),
                    exhibit.getExhibitHall().getName(),
                    exhibit.getKeywords().stream()
                            .map(Keyword::getKeywordId)
                            .collect(Collectors.toSet())
            );
        }
    }

    public boolean isMultiple() {
        return exhibits.size() > 1;
    }

    public ExhibitSummary representative() {
        return exhibits.get(0);
    }

    public int additionalCount() {
        return exhibits.size() - 1;
    }
}
