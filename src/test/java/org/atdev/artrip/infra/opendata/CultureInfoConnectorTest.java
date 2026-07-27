package org.atdev.artrip.infra.opendata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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

    @Test
    @DisplayName("sourceCode()는 KCISA_CULTURE_INFO 상수를 반환한다")
    void sourceCode_returnsExpectedConstant() {
        assertThat(connector.sourceCode()).isEqualTo(CultureInfoConnector.SOURCE_CODE);
        assertThat(connector.sourceCode()).isEqualTo("KCISA_CULTURE_INFO");
    }

    @Test
    @DisplayName("응답 파싱/통신 중 예외가 발생하면 빈 CanonicalPage(totalCount=0)를 반환한다")
    void fetchPage_whenRestTemplateThrows_returnsEmptyPage() {
        // given
        when(restTemplate.getForObject(any(URI.class), eq(byte[].class)))
                .thenThrow(new RestClientException("connection failed"));

        // when
        CanonicalPage result = connector.fetchPage(1, 100);

        // then
        assertAll(
                () -> assertThat(result.totalCount()).isEqualTo(0),
                () -> assertThat(result.items()).isEmpty()
        );
    }

    @Test
    @DisplayName("응답 파싱 중 예외가 발생하면 fetchDetail은 null을 반환한다")
    void fetchDetail_whenRestTemplateThrows_returnsNull() {
        // given
        when(restTemplate.getForObject(any(URI.class), eq(byte[].class)))
                .thenThrow(new RestClientException("connection failed"));

        // when
        CanonicalExhibitDetail detail = connector.fetchDetail("1");

        // then
        assertThat(detail).isNull();
    }

    @Test
    @DisplayName("items가 없는 응답이면 fetchDetail은 null을 반환한다")
    void fetchDetail_whenNoItems_returnsNull() {
        // given
        stubResponse("""
                <?xml version="1.0" encoding="UTF-8"?>
                <response><header><resultCode>00</resultCode></header><body><items></items></body></response>
                """);

        // when
        CanonicalExhibitDetail detail = connector.fetchDetail("999");

        // then
        assertThat(detail).isNull();
    }

    @Test
    @DisplayName("body의 totalCount가 없으면 0으로 처리된다")
    void fetchPage_missingTotalCount_defaultsToZero() {
        // given
        stubResponse("""
                <?xml version="1.0" encoding="UTF-8"?>
                <response><header><resultCode>00</resultCode></header>
                <body><items>%s</items></body></response>
                """.formatted(item("1", "전시", "20250101", "20991231")));

        // when
        CanonicalPage result = connector.fetchPage(1, 100);

        // then
        assertAll(
                () -> assertThat(result.totalCount()).isEqualTo(0),
                () -> assertThat(result.items()).hasSize(1)
        );
    }

    @Test
    @DisplayName("빈 thumbnail은 posterUrl을 null로 만들고, place/area/gps 값은 그대로 매핑된다")
    void fetchPage_mapsFieldsAndBlankThumbnailToNull() {
        // given
        String noThumbnailItem = """
                <item><seq>3</seq><title>전시명</title><startDate>20250101</startDate><endDate>20991231</endDate>
                <place>국립중앙박물관</place><area>서울</area>
                <thumbnail></thumbnail>
                <gpsX>126.98023689950804</gpsX><gpsY>37.52406305836529</gpsY></item>
                """;
        stubResponse(listXml(noThumbnailItem));

        // when
        CanonicalPage result = connector.fetchPage(1, 100);

        // then
        assertThat(result.items()).hasSize(1);
        var canonical = result.items().get(0);
        assertAll(
                () -> assertThat(canonical.externalId()).isEqualTo("3"),
                () -> assertThat(canonical.placeName()).isEqualTo("국립중앙박물관"),
                () -> assertThat(canonical.region()).isEqualTo("서울"),
                () -> assertThat(canonical.posterUrl()).isNull(),
                () -> assertThat(canonical.latitude()).isEqualByComparingTo(new BigDecimal("37.52406305836529")),
                () -> assertThat(canonical.longitude()).isEqualByComparingTo(new BigDecimal("126.98023689950804"))
        );
    }

    @Test
    @DisplayName("gps 좌표가 숫자로 파싱되지 않으면 해당 항목만 skip 된다")
    void fetchPage_invalidGps_skipsOnlyThatItem() {
        // given
        String invalidGps = """
                <item><seq>1</seq><title>깨진좌표</title><startDate>20250101</startDate><endDate>20991231</endDate>
                <place>국립중앙박물관</place><area>서울</area>
                <thumbnail>http://img.jpg</thumbnail>
                <gpsX>N/A</gpsX><gpsY>N/A</gpsY></item>
                """;
        String valid = item("2", "정상", "20250101", "20991231");
        stubResponse(listXml(invalidGps + valid));

        // when
        CanonicalPage result = connector.fetchPage(1, 100);

        // then
        assertAll(
                () -> assertThat(result.items()).hasSize(1),
                () -> assertThat(result.items().get(0).externalId()).isEqualTo("2")
        );
    }

    @Test
    @DisplayName("fetchPage는 요청 URI에 페이지 번호와 페이지 크기를 포함한다")
    void fetchPage_buildsUriWithPageParameters() {
        // given
        stubResponse(listXml(item("1", "전시", "20250101", "20991231")));

        // when
        connector.fetchPage(2, 50);

        // then
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        verify(restTemplate).getForObject(captor.capture(), eq(byte[].class));
        String uri = captor.getValue().toString();
        assertAll(
                () -> assertThat(uri).contains("PageNo=2"),
                () -> assertThat(uri).contains("numOfrows=50"),
                () -> assertThat(uri).contains("test-key")
        );
    }

}
