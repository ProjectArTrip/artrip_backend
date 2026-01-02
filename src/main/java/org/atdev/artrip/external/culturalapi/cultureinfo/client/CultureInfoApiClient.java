package org.atdev.artrip.external.culturalapi.cultureinfo.client;

import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.request.CultureInfoRequest;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response.CultureInfoDetailResponse;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response.CultureInfoItem;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response.CultureInfoListResponse;
import org.atdev.artrip.external.culturalapi.properties.PublicDataProperties;
import org.atdev.artrip.global.apipayload.exception.ExternalApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class CultureInfoApiClient extends BasePublicDataClient<CultureInfoItem, CultureInfoRequest, CultureInfoListResponse> {

    private static final String SERVICE_TP = "A";
    private static final String PATH_REALM2 = "/realm2";
    private static final String PATH_DETAILS2 = "/detail2";

    public CultureInfoApiClient(
            WebClient publicDataWebClient,
            PublicDataProperties properties,
            RetryRegistry retryRegistry) {
        super(publicDataWebClient, properties, retryRegistry);
    }

    @Override
    protected String getApiName() {
        return "cultureInfoApi";
    }

    @Override
    protected String getApiPath() {
        return PATH_REALM2;
    }

    @Override
    protected ParameterizedTypeReference<CultureInfoListResponse> getResponseTypeRef() {
        return new ParameterizedTypeReference<>() {
        };
    }

    @Override
    protected Class<CultureInfoListResponse> getResponseClass() {
        return CultureInfoListResponse.class;
    }

    @Override
    protected void addRequestParams(UriComponentsBuilder builder, CultureInfoRequest request) {
        builder.queryParam("serviceTp", SERVICE_TP);

        if (request.getFrom() != null) {
            builder.queryParam("from", request.getFrom());
        }

        if (request.getTo() != null) {
            builder.queryParam("to", request.getTo());
        }

        if (request.getSido() != null) {
            builder.queryParam("sido", request.getSido());
        }

        if (request.getKeyword() != null) {
            builder.queryParam("keyword", request.getKeyword());
        }

        if (request.getPlace() != null) {
            builder.queryParam("place", request.getPlace());
        }
    }

    public CultureInfoListResponse fetchFromDate(String from, int pageNo) {
        log.debug("지정 날짜 이후 전시 목록 조회 : {}", pageNo);
        CultureInfoRequest request = CultureInfoRequest.builder()
                .serviceKey(properties.getServiceKey())
                .pageNo(pageNo)
                .numOfRows(properties.getPageSize())
                .from(from)
                .build();
        return fetch(request);
    }

    public CultureInfoListResponse fetchByPeriod(String from, String to, int pageNo) {
        log.debug("realm2 기간별 전시 목록 조회 : {} ======", pageNo);
        CultureInfoRequest request = CultureInfoRequest.builder()
                .serviceKey(properties.getServiceKey())
                .pageNo(pageNo)
                .numOfRows(properties.getPageSize())
                .from(from)
                .to(to)
                .build();
        return fetch(request);
    }

    public CultureInfoListResponse fetchExhibits(int pageNo) {
        log.debug("realm 전체 목록 조회 : {}", pageNo);
        CultureInfoRequest request = CultureInfoRequest.builder()
                .serviceKey(properties.getServiceKey())
                .pageNo(pageNo)
                .numOfRows(properties.getPageSize())
                .build();
        return fetch(request);
    }

    public CultureInfoDetailResponse fetchDetails(String idx) {
        URI uri = buildDetailUri(idx);
        log.debug("상세 조회 URI: {}", uri);

        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(
                                            new ExternalApiException(getApiName(), response.statusCode(), body))))
                    .bodyToMono(CultureInfoDetailResponse.class)
                    .doOnSuccess(this::logDetailResponse)
                    .doOnError(e -> log.error("상세 조회 에러 - idx :{} error : {}", idx, e.getMessage()))
                    .block();
        } catch (Exception e) {
            log.error("상세 API 호출 실패 - idx :{} error : {}", idx, e.getMessage());
            return null;
        }
    }

    private URI buildDetailUri(String idx) {
        String queryString = UriComponentsBuilder.newInstance()
                .queryParam("seq", idx)
                .build()
                .getQuery();

        String finalUrl = properties.getBaseUrl() + PATH_DETAILS2 +
                "?serviceKey=" + properties.getServiceKey() + "&" + queryString;

        return URI.create(finalUrl);
    }

    private void logDetailResponse(CultureInfoDetailResponse response) {
        if (response == null) {
            log.warn("상세 응답 null : {}", getApiName());
            return;
        }
        if (response.isSuccess() && response.hasData()) {
            log.debug("상세 조회 - title : {}", response.getItem().getTitle());
        } else {
            log.warn("상세 조회 실패 - error: {}", response.getErrorMessage());
        }
    }
}