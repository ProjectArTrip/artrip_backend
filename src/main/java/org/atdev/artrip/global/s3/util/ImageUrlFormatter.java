package org.atdev.artrip.global.s3.util;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.global.s3.service.S3Service;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageUrlFormatter {

    private final S3Service s3Service;

    public String getResizedUrl(String originalUrl, Integer w, Integer h, String f) {
        return s3Service.buildResizeUrl(originalUrl, w, h, f);
    }
    public void resizePosterUrls(List<HomeListResponse> responses, Integer w, Integer h, String f) {
        if (responses == null || responses.isEmpty()) return;

        responses.forEach(response -> {
            String originalUrl = response.getPosterUrl();
            String resizedUrl = getResizedUrl(originalUrl, w, h, f);
            response.setPosterUrl(resizedUrl);
        });
    }
}