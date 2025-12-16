package org.atdev.artrip.external.culturalapi.cultureinfo.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "response")
public class CultureInfoListResponse extends BaseCultureInfoResponse<List<CultureInfoItem>>
        implements PublicDataResponse<CultureInfoItem>  {

    @JacksonXmlProperty(localName = "body")
    private ListBody body;

    @Override
    public boolean hasData() {
        return body != null && body.getItems() != null && !body.getItems().isEmpty();
    }

    @Override
    public List<CultureInfoItem> getResult() {
        return getItems();
    }

    @Override
    public int getTotalPages() {
        if (body == null || body.getNumOfrows() == 0) {
            return 0;
        }
        return (int) Math.ceil((double) body.getTotalCount() / body.getNumOfrows());
    }

    @Override
    public int getCurrentPage() {
        return body != null ? body.getPageNo() : 0;
    }

    @Override
    public int getTotalCount() {
        return body != null ? body.getTotalCount() : 0;
    }

    @Override
    public List<CultureInfoItem> getItems() {
        if(body ==null || body.getItems() ==null) {
            return Collections.emptyList();
        }
        return body.getItems();
    }

    public boolean isLastPage() {
        return getCurrentPage() >= getTotalPages();
    }

    public List<CultureInfoItem> getExhibits() {
        return getItems().stream()
                .filter(CultureInfoItem::isExhibition)
                .toList();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListBody {
        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;

        @JacksonXmlProperty(localName = "PageNo")
        private int pageNo;

        @JacksonXmlProperty(localName = "numOfrows")
        private int numOfrows;

        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "item")
        private List<CultureInfoItem> items;
    }
}
