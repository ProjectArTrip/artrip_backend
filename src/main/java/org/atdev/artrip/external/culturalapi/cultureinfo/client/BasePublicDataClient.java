package org.atdev.artrip.external.culturalapi.cultureinfo.client;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.request.BasePublicDataRequest;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response.BasePublicDataItem;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response.PublicDataResponse;
import org.atdev.artrip.external.culturalapi.properties.PublicDataProperties;
import org.atdev.artrip.global.apipayload.exception.ExternalApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Supplier;

@Slf4j
public abstract class BasePublicDataClient <
        T extends BasePublicDataItem,
        R extends BasePublicDataRequest,
        S extends PublicDataResponse<T>>{

    protected final WebClient webClient;
    protected final PublicDataProperties properties;
    protected final Retry retry;

    protected BasePublicDataClient(WebClient publicDataWebClient,
                                   PublicDataProperties properties,
                                   RetryRegistry retryRegistry) {
        this.webClient = publicDataWebClient;
        this.properties = properties;
        this.retry = retryRegistry.retry("publicDataAPI");
    }

    protected abstract String getApiName();

    protected abstract String getApiPath();

    protected abstract ParameterizedTypeReference<S> getResponseTypeRef();

    protected abstract Class<S> getResponseClass();

    protected abstract void addRequestParams(UriComponentsBuilder builder, R request);

    protected S fetch(R request) {
        Supplier<S> supplier = Retry.decorateSupplier(retry, () -> doFetch(request));

        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("[{}] API 호출 실패: {}", getApiName(), e.getMessage());
            throw new ExternalApiException(getApiName(), "API 호출 실패", e);
        }
    }

    protected S doFetch(R request) {
        URI uri = buildUri(request);

        log.debug("[{}] 요청 URI: {}", getApiName(), uri);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new ExternalApiException(getApiName(), response.statusCode(), body))))
                .bodyToMono(getResponseClass())
                .doOnSuccess(response -> logResponse(response))
                .doOnError(e -> log.error("[{}] 호출 에러: {}", getApiName(), e.getMessage()))
                .block();
    }

    protected URI buildUri(R request) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl())
                .path(getApiPath())
                .queryParam("PageNo", request.getPageNo())
                .queryParam("numOfrows", request.getNumOfRows())
                .queryParam("sortStdr",  request.getSortStdr());

        addRequestParams(builder, request);

        String queryString = builder.build().getQuery();

        String finalUrl = properties.getBaseUrl() + getApiPath() +
                "?serviceKey=" +properties.getServiceKey() + "&" + queryString;

        return URI.create(finalUrl);
    }

    protected void logResponse(S response) {
        if (response.isSuccess()) {
            log.info("[{}] 조회 성공 - 총 건수 {} : 페이지 : {}/{}",
                    getApiName(),
                    response.getTotalCount(),
                    response.getCurrentPage(),
                    response.getTotalPages());
        } else {
            log.warn("{} API 응답 실패 - {}" , getApiName(), response.getErrorMessage());
        }
    }
}
