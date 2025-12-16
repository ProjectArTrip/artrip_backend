package org.atdev.artrip.external.culturalapi.cultureinfo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CultureInfoRequest extends BasePublicDataRequest{

    private String from;
    private String to;
    private String sido;
    private String keyword;
    private String realmCode;
    private String place;

}
