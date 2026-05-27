package org.atdev.artrip.service.dto.result;

import org.atdev.artrip.utils.page.PageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record AdminExhibitListResult(
        List<AdminExhibitListItemResult> exhibits,
        PageDTO pageInfo
) {
    public static AdminExhibitListResult from(Page<AdminExhibitListItemResult> page) {
        return new AdminExhibitListResult(page.getContent(), PageDTO.from(page));
    }
}
