package org.atdev.artrip.global.s3.web.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageResizeRequest {

    @Schema(description = "width", defaultValue = "100")
    private Integer w;

    @Schema(description = "height", defaultValue = "100")
    private Integer h;

    @Schema(defaultValue = "webp")
    private String f = "webp";

}
