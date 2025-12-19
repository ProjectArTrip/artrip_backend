package org.atdev.artrip.external.culturalapi.cultureinfo.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicDataHeader {

    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;

    @JacksonXmlProperty(localName = "resultMsg")
    private String resultMsg;

    public boolean isSuccess() {
        return "00".equals(resultCode);
    }
}
