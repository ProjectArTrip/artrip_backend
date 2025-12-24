package org.atdev.artrip.domain.home.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegionResponse {
    private String region;
    private String imageUrl;
}