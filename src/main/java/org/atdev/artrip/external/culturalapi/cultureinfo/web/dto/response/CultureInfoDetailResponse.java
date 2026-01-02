package org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "response")
public class CultureInfoDetailResponse extends BaseCultureInfoResponse<CultureInfoDetailItem> {

    @JacksonXmlProperty(localName = "body")
    private DetailBody body;

    @Override
    public boolean hasData() {
        return body != null && body.getItems() != null && body.getItems().getItem() != null;
    }

    @Override
    public CultureInfoDetailItem getResult() {
        return getItem();
    }

    public CultureInfoDetailItem getItem() {
        if (body != null && body.getItems() != null) {
            return body.getItems().getItem();
        }
        return null;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetailBody {
        @JacksonXmlProperty(localName = "items")
        private Items items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        @JacksonXmlProperty(localName = "item")
        private CultureInfoDetailItem item;
    }
}
