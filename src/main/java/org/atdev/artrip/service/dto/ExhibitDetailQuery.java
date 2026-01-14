package org.atdev.artrip.service.dto;

import lombok.Builder;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;

@Builder
public record ExhibitDetailQuery(
        Long exhibitId,
        Long userId,
        Integer width,
        Integer height,
        String format
) {

    public static ExhibitDetailQuery of(Long id, Long userId, ImageResizeRequest resize) {
        return ExhibitDetailQuery.builder()
                .exhibitId(id)
                .userId(userId)
                .width(resize.getW())
                .height(resize.getH())
                .format(resize.getF())
                .build();
    }
}