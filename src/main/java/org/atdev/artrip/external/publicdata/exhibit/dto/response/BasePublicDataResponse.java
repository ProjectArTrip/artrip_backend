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
public class BasePublicDataResponse<T extends BasePublicDataItem> {

    @JacksonXmlProperty(localName = "header")
    private Header header;

    @JacksonXmlProperty(localName = "body")
    private Body<T> body;

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
    public static class Body<T> {
        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;

        @JacksonXmlProperty(localName = "pageNo")
        private int pageNo;

        @JacksonXmlProperty(localName = "numOfRows")
        private int numOfRows;

        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "item")
        private List<T> items;
    }

    public boolean isSuccess() {
        return header != null && "0000".equals(header.getResultCode());
    }

    public String getErrorMessage() {
        if (header == null) {
            return "응답 헤더가 없습니다.";
        }
        return String.format("[%s] %s", header.getResultCode(), header.getResultMsg());
    }

    public int getTotalPages() {
        if (body == null || body.getNumOfRows() == 0 ) {
            return 0;
        }
        return (int) Math.ceil((double) body.getTotalCount() / body.getNumOfRows());
    }

    public int getCurrentPage() {
        return body != null ? body.getPageNo() : 0;
    }

    public int getTotalCount() {
        return body != null ? body.getTotalCount() : 0;
    }

    public List<T> getItems() {
        if (body == null || body.getItems() == null) {
            return Collections.emptyList();
        }
        return body.getItems();
    }

    public boolean hasData() {
        return !getItems().isEmpty();
    }

    public boolean isLastPage() {
        return getCurrentPage() >= getTotalPages();
    }
}
