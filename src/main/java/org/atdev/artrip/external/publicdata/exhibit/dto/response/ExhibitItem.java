package org.atdev.artrip.external.publicdata.exhibit.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExhibitItem implements BasePublicDataItem{

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "collectionDb")
    private String collectionDb;  // 전시정보

    @JacksonXmlProperty(localName = "subjectCategory")
    private String subjectCategory;  // 국내전시/해외전시

    @JacksonXmlProperty(localName = "rights")
    private String rights;  // 미술관명 (예: 국립현대미술관)

    @JacksonXmlProperty(localName = "charge")
    private String charge;  // 요금

    @JacksonXmlProperty(localName = "venue")
    private String venue;  // 전시 장소 (예: 1전시실)

    @JacksonXmlProperty(localName = "eventPeriod")
    private String eventPeriod;  // 기간 (예: 2020-12-17 ~ 2021-04-11)

    @JacksonXmlProperty(localName = "subDescription")
    private String subDescription;  // 설명

    @JacksonXmlProperty(localName = "person")
    private String person;  // 작가

    @JacksonXmlProperty(localName = "creator")
    private String creator;

    @JacksonXmlProperty(localName = "publisher")
    private String publisher;

    // place로 사용 (미술관명)
    public String getPlace() {
        return this.rights;
    }

    // 기존 호환성 메서드
    public String getThumbnail() {
        return null;  // KCISA API에서 이미지 없음
    }

    public String getUrl() {
        return null;  // KCISA API에서 URL 없음
    }

    public String getPlaceAddr() {
        return null;
    }

    public String getArea() {
        return null;
    }

    public String getPhone() {
        return null;
    }

    public String getGpsX() {
        return null;
    }

    public String getGpsY() {
        return null;
    }

    @Override
    public String getUniqueId() {
        return title + "_" + (eventPeriod != null ? eventPeriod : "");
    }

    @Override
    public boolean isValid() {
        return title != null && !title.isBlank();
    }
}
