package org.atdev.artrip.external.publicdata.exhibit.dto.response;

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
public class ExhibitResponse implements PublicDataResponse<ExhibitItem> {

    @JacksonXmlProperty(localName = "header")
    private Header header;

    @JacksonXmlProperty(localName = "body")
    private ExhibitBody body;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;

        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExhibitBody {
        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;

        @JacksonXmlProperty(localName = "pageNo")
        private int pageNo;

        @JacksonXmlProperty(localName = "numOfRows")
        private int numOfRows;

        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "item")
        private List<ExhibitItem> items;
    }

    @Override
    public boolean isSuccess() {
        return header != null && "0000".equals(header.getResultCode());
    }

    @Override
    public String getErrorMessage() {
        if (header == null) {
            return "응답 헤더가 없습니다.";
        }
        return String.format("[%s] %s", header.getResultCode(), header.getResultMsg());
    }

    @Override
    public int getTotalPages() {
        if (body == null || body.getNumOfRows() == 0) {
            return 0;
        }
        return (int) Math.ceil((double) body.getTotalCount() / body.getNumOfRows());
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
    public List<ExhibitItem> getItems() {
        if (body == null || body.getItems() == null) {
            return Collections.emptyList();
        }
        return body.getItems();
    }

    public List<ExhibitItem> getExhibitsOnly() {
        return getItems().stream()
                .filter(item -> "전시정보".equals(item.getCollectionDb()))
                .toList();
    }
}
