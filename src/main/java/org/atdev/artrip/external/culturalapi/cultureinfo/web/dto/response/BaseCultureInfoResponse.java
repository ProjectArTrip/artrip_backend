package org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseCultureInfoResponse<T>{

    @JacksonXmlProperty(localName = "header")
    protected PublicDataHeader header;

    public boolean isSuccess() {
        return header != null && header.isSuccess();
    }

    public String getErrorMessage() {
        if (header == null) {
            return "응답 헤더가 없습니다.";
        }
        return String.format("[%s] %s", header.getResultCode(), header.getResultMsg());
    }

    public abstract boolean hasData();

    public abstract T getResult();
}
