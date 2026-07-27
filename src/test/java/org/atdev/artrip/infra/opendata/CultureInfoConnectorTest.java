package org.atdev.artrip.infra.opendata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CultureInfoConnectorTest {

    @Mock
    RestTemplate restTemplate;

    CultureInfoConnector connector;

    @BeforeEach
    void setUp() {
        connector = new CultureInfoConnector(restTemplate, "https://base", "test-key");
    }

    private void stubResponse(String xml) {
        when(restTemplate.getForObject(any(URI.class), eq(byte[].class))).thenReturn(xml.getBytes(StandardCharsets.UTF_8));
    }

    private String listXml(String items) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <response><header><resultCode>00</resultCode></header>
                <body><totalCount>1</totalCount><items>%s</items></body></response>
                """.formatted(items);
    }

    private String item(String seq, String title, String start, String end) {
        return """
                <item><seq>%s</seq><title>%s</title><startDate>%s</startDate><endDate>%s</endDate>
                <place>국립중앙박물관</place><realmName>전시</realmName><area>서울</area>
                <thumbnail>http://img.jpg</thumbnail>
                <gpsX>126.98023689950804</gpsX><gpsY>37.52406305836529</gpsY></item>
                """.formatted(seq, title, start, end);
    }

    @Test
    @DisplayName("종료일이 지난 전시는 결과에서 제외된다")
    void fetchPage_pastExhibit_excluded() {
        // given
        String past = item("1", "종료전시", "20200101", LocalDate.now().minusDays(1).toString().replace("-", ""));
        String active = item("2", "진행전시", "20200101", "20991231");
        stubResponse(listXml(past + active));

        // when
        CanonicalPage result = connector.fetchPage(1, 100);
        System.out.println(result);

        // then
        assertAll(
                () -> assertThat(result.totalCount()).isEqualTo(1),
                () -> assertThat(result.items().get(0).externalId())
        );
    }

    @Test
    @DisplayName("날짜가 비정상인 항목만 skip 하고 나머지는 정상 반환한다")
    void fetchPage_malformedDate_skipsOnlyThatItem() {
        // given
        String broken = item("1", "깨진날짜", "", "20991231");
        String valid = item("2", "정상", "20250101", "20991231");
        stubResponse(listXml(broken + valid));

        // when
        CanonicalPage result = connector.fetchPage(1, 100);
        System.out.println(result);

        // then

        assertAll(
                () -> assertThat(result.totalCount()).isEqualTo(1),
                () -> assertThat(result.items().get(0).externalId()).isEqualTo("2")
        );
    }

    @Test
    @DisplayName("제목의 HTML 엔티티는 디코드된다")
    void fetchPage_htmlEntity_decoded() {
        // given
        stubResponse(listXml(item("1", "체험전 &amp;lt;조선&amp;gt;", "20250101", "20991231")));

        // when
        CanonicalPage result = connector.fetchPage(1, 100);

        System.out.println(result);

        // then
        assertAll(
                () -> assertThat(result.items().get(0).title()).isEqualTo("체험전 <조선>")
        );
    }

    @Test
    @DisplayName("상세의 빈 contents1 은 null 로 변환된다 — 빈 문자열로 기존 값을 덮지 않기 위함")
    void fetchDetail_blankContents_becomesNull() {
        // given
        stubResponse("""
                <?xml version="1.0" encoding="UTF-8"?>
                <response><header><resultCode>00</resultCode></header><body><items><item>
                <seq>295979</seq><price>무료</price><contents1></contents1>
                <url>https://ticket.example</url><phone>031-5183-3200</phone>
                <placeAddr>경기도 화성시</placeAddr><placeUrl>http://place.example</placeUrl>
                </item></items></body></response>
                """);

        // when
        CanonicalExhibitDetail detail = connector.fetchDetail("295979");

        // then
        assertAll(
                () -> assertThat(detail.description()).isNull(),
                () -> assertThat(detail.ticketUrl()).isEqualTo("https://ticket.example"),
                () -> assertThat(detail.placeAddr()).isEqualTo("경기도 화성시")
        );
    }

}
