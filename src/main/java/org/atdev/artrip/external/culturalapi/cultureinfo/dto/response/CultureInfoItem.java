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
public class CultureInfoItem implements BasePublicDataItem{


    @JacksonXmlProperty(localName = "seq")
    private String seq;

    @JacksonXmlProperty(localName = "serviceName")
    private String serviceName;

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "startDate")
    private String startDate;

    @JacksonXmlProperty(localName = "endDate")
    private String endDate;

    @JacksonXmlProperty(localName = "place")
    private String place;

    @JacksonXmlProperty(localName = "realmName")
    private String realmName;

    @JacksonXmlProperty(localName = "area")
    private String area;

    @JacksonXmlProperty(localName = "sigungu")
    private String sigungu;

    @JacksonXmlProperty(localName = "thumbnail")
    private String thumbnail;

    @JacksonXmlProperty(localName = "gpsX")
    private String gpsX;

    @JacksonXmlProperty(localName = "gpsY")
    private String gpsY;

    @Override
    public String getUniqueId() {
        return seq != null ? seq : (title + "_" + startDate);
    }

    @Override
    public boolean isValid() {
        return title != null && !title.isBlank();
    }

    public boolean isExhibition() {
        return "전시".equals(realmName);
    }

    public String getFullAddress() {
        if (area == null) return null;
        if (sigungu != null && !sigungu.isBlank()) {
            return area + " " + sigungu;
        }
        return area;
    }
}
