package org.atdev.artrip.global.s3.util;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageUrlFormatter {

    private final S3Service s3Service;

public String getResizedUrl(String originalUrl, Integer w, Integer h, String f) {
    return s3Service.buildResizeUrl(originalUrl, w, h, f);
}
    public List<ExhibitRandomResult> resizePosterUrls(List<ExhibitRandomResult> responses, Integer w, Integer h, String f) {
        if (responses == null || responses.isEmpty()) return List.of();

        return responses.stream()
                .map(res -> res.withResizeUrl(getResizedUrl(res.posterUrl(), w, h, f)))
                .toList();
    }
}