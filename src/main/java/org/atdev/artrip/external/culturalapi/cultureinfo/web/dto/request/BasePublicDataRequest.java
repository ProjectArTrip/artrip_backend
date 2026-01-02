package org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BasePublicDataRequest {

    protected String serviceKey;

    @Builder.Default
    protected int pageNo = 1;

    @Builder.Default
    protected int numOfRows = 50;

    @Builder.Default
    protected String sortStdr = "1";
}
