package org.atdev.artrip.controller.dto.response;


import lombok.Builder;
import lombok.Getter;
import org.atdev.artrip.domain.exhibit.Exhibit;

import java.util.List;

@Getter
@Builder
public class ExhibitFilterResponse {
    private List<Exhibit> content;
    private Long nextCursorId;
    private boolean hasNext;
}