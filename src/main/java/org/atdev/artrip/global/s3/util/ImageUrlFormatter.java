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

}