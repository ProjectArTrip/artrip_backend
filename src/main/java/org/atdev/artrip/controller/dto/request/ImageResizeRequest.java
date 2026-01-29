package org.atdev.artrip.controller.dto.request;

public record ImageResizeRequest(
        Integer w,
        Integer h,
        String f
) {

    public ImageResizeRequest {
        w = (w == null || w <= 0) ? 100 : w;
        h = (h == null || h <= 0) ? 100 : h;

        f = (f == null || f.isBlank()) ? "webp" : f;
    }
}
