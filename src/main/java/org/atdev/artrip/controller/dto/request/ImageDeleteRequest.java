package org.atdev.artrip.controller.dto.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageDeleteRequest {
    private List<String> imageUrls;
}