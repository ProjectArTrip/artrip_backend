package org.atdev.artrip.global.s3;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageDeleteRequest {
    private List<String> imageUrls;
}