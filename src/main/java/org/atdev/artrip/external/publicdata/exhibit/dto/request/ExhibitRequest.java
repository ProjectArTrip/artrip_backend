package org.atdev.artrip.external.publicdata.exhibit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitRequest extends BasePublicDataRequest{

    private String from;
    private String to;
    private String sido;
    private String keyword;
    private String realmCode;

}
