package org.atdev.artrip.infra.opendata;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Order(1)
public class CultureInfoConnector implements ExhibitSourceConnector {

    public static final String SOURCE_CODE = "KCISA_CULTURE_INFO";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;
    private final String baseUrl;
    private final String serviceKey;

    public CultureInfoConnector(
            @Qualifier("opendataRestTemplate") RestTemplate restTemplate,
            @Value("${opendata.culture.base-url}") String baseUrl,
            @Value("${opendata.culture.service-key}") String serviceKey
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.serviceKey = serviceKey;
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String sourceCode() {
        return SOURCE_CODE;
    }

    @Override
    public CanonicalPage fetchPage(int pageNo, int size) {
        URI uri = URI.create(baseUrl + "/realm2?serviceKey=" + serviceKey + "&PageNo=" + pageNo + "&numOfrows=" + size + "&realmCode=D000&serviceTp=A");

        CultureInfoResponse response = read(uri);
        int totalCount = (response == null || response.body() == null || response.body().totalCount() == null ) ? 0 : response.body().totalCount();

        List<CanonicalExhibit> items = items(response).stream()
                .map(this::toCanonical)
                .flatMap(Optional::stream)
                .toList();

        return new CanonicalPage(items, totalCount);
    }

    @Override
    public CanonicalExhibitDetail fetchDetail(String externalId) {
        URI uri = URI.create(baseUrl + "/detail2?serviceKey=" + serviceKey + "&seq=" + externalId);

        List<CultureInfoResponse.Item> items = items(read(uri));
        if (items.isEmpty()) {
            return null;
        }
        CultureInfoResponse.Item item = items.get(0);
        return new CanonicalExhibitDetail(
                blankToNull(item.contents1()), blankToNull(item.url()), blankToNull(item.phone()),
                blankToNull(item.placeAddr()), blankToNull(item.placeUrl()));
    }

    private CultureInfoResponse read(URI uri) {
        try {

            byte[] xml = restTemplate.getForObject(uri, byte[].class);
            return xmlMapper.readValue(xml, CultureInfoResponse.class);

        } catch (Exception e) {
            log.error("[exhibit-sync] {} 응답 파싱 에러 - uri 경로 : {}", SOURCE_CODE, uri.getPath(), e);
            return null;
        }
    }

    private List<CultureInfoResponse.Item> items(CultureInfoResponse response) {
        if (response == null || response.body() == null || response.body().items() == null || response.body().items().item() == null ) {
            return List.of();
        }

        return response.body().items().item();
    }

    private Optional<CanonicalExhibit> toCanonical(CultureInfoResponse.Item item) {
        try {
            LocalDate startDate = LocalDate.parse(item.startDate(), DATE_FORMAT);
            LocalDate endDate = LocalDate.parse(item.endDate(), DATE_FORMAT);
            if (endDate.isBefore(LocalDate.now())) {
                return Optional.empty();
            }
            return Optional.of(new CanonicalExhibit(
                    item.seq(),
                    HtmlUtils.htmlUnescape(item.title()).trim(),
                    startDate,
                    endDate,
                    HtmlUtils.htmlUnescape(item.place()).trim(),
                    item.area(),
                    blankToNull(item.thumbnail()),
                    new BigDecimal(item.gpsY()),
                    new BigDecimal(item.gpsX())
            ));
        } catch (Exception e) {
            log.warn("[exhibit-sync] {} item 변환 에러 - seq={} 원인 = {}", SOURCE_CODE, item.seq(), e.getMessage());
            return Optional.empty();
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank() ? null : value);
    }
}
