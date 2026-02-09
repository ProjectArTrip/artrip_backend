package org.atdev.artrip.controller.dto.request;

import java.util.List;

public record ClusterRequestDto(
        List<Long> ids
) {
}
