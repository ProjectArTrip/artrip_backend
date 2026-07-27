package org.atdev.artrip.infra.opendata;

import java.util.List;

public record CanonicalPage(
        List<CanonicalExhibit> items,
        int totalCount
) {
}
