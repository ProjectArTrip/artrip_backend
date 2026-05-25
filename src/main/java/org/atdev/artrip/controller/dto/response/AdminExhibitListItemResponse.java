package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.service.dto.result.AdminExhibitListItemResult;
import org.atdev.artrip.utils.DateTimeUtils;

public record AdminExhibitListItemResponse(
        Long exhibitId,
        String title,
        String exhibitPeriod,
        String country,
        String region,
        Status status,
        String hallName
) {

    public static AdminExhibitListItemResponse from(AdminExhibitListItemResult item) {
        return new AdminExhibitListItemResponse(
                item.exhibitId(),
                item.title(),
                DateTimeUtils.convertDate(item.startDate(), item.endDate()),
                item.country(),
                item.region(),
                item.status(),
                item.hallName()
        );
    }

}
