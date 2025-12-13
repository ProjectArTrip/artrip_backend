package org.atdev.artrip.external.publicdata.exhibit.client;

import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.external.publicdata.exhibit.dto.request.ExhibitRequest;
import org.atdev.artrip.external.publicdata.exhibit.dto.response.ExhibitItem;
import org.atdev.artrip.external.publicdata.exhibit.dto.response.ExhibitResponse;
import org.atdev.artrip.external.publicdata.properties.PublicDataProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class ExhibitApiClient extends BasePublicDataClient<ExhibitItem, ExhibitRequest, ExhibitResponse>{

    public ExhibitApiClient(
            WebClient publicDataWebClient,
            PublicDataProperties properties,
            RetryRegistry retryRegistry){
        super(publicDataWebClient, properties, retryRegistry);
    }

    @Override
    protected String getApiName() {
        return "exhibitApi";
    }

    @Override
    protected String getApiPath() {
        return "";
    }

    @Override
    protected ParameterizedTypeReference<ExhibitResponse> getResponseTypeRef() {
        return new ParameterizedTypeReference<>(){};
    }

    @Override
    protected Class<ExhibitResponse> getResponseClass() {
        return ExhibitResponse.class;
    }

    @Override
    protected void addRequestParams(UriComponentsBuilder builder, ExhibitRequest request) {
        if (request.getFrom() != null) {
            builder.queryParam("from", request.getFrom());
        }

        if(request.getTo() != null) {
            builder.queryParam("to", request.getTo());
        }

        if (request.getSido() != null) {
            builder.queryParam("sido", request.getSido());
        }

        if (request.getRealmCode() != null) {
            builder.queryParam("realmCode", request.getRealmCode());
        }
    }

    public ExhibitResponse fetchByPeriod(String from, String to, int pageNo) {
        ExhibitRequest request = ExhibitRequest.builder()
                .serviceKey(properties.getServiceKey())
                .pageNo(pageNo)
                .numOfRows(properties.getPageSize())
                .from(from)
                .to(to)
                .build();

        return fetch(request);
    }

    public ExhibitResponse fetchExhibits(int pageNo) {
        ExhibitRequest request = ExhibitRequest.builder()
                .serviceKey(properties.getServiceKey())
                .pageNo(pageNo)
                .numOfRows(properties.getPageSize())
                .build();
        return fetch(request);
    }
}
