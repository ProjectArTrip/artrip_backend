package org.atdev.artrip.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.atdev.artrip.utils.StringUtils;

@Getter
@Builder
public class ImageResponse {

    private String originalUrl;
    private String posterUrl;

    public static ImageResponse of(String originalUrl, String posterUrl) {
        return ImageResponse.builder()
                .originalUrl(originalUrl)
                .posterUrl(StringUtils.emptyIfNull(posterUrl))
                .build();
    }
}
