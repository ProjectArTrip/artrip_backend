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
public class CultureInfoDetailItem {

    @JacksonXmlProperty(localName = "seq")
    private String seq;

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "price")
    private String price;

    @JacksonXmlProperty(localName = "contents1")
    private String contents1;

    @JacksonXmlProperty(localName = "url")
    private String url;

    @JacksonXmlProperty(localName = "phone")
    private String phone;

    @JacksonXmlProperty(localName = "imgUrl")
    private String imgUrl;

    @JacksonXmlProperty(localName = "placeUrl")
    private String placeUrl;

    @JacksonXmlProperty(localName = "placeAddr")
    private String placeAddr;

    @JacksonXmlProperty(localName = "placeSeq")
    private String placeSeq;
}
