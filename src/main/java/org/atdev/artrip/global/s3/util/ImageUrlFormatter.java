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

    public void resizePosterUrls(List<HomeListResponse> responses, ImageResizeRequest resize) {
        if (resize == null) return;
        responses.forEach(r -> r.setPosterUrl(
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.getW(), resize.getH(), resize.getF())
        ));
    }
}