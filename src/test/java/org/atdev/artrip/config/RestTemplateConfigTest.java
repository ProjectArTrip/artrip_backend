package org.atdev.artrip.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

    private final RestTemplateConfig config = new RestTemplateConfig();

    @Test
    @DisplayName("restTemplate 빈은 HttpComponentsClientHttpRequestFactory 기반으로 생성된다")
    void restTemplate_usesHttpComponentsRequestFactory() {
        RestTemplate restTemplate = config.restTemplate();

        assertThat(restTemplate).isNotNull();
        assertThat(restTemplate.getRequestFactory()).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
    }

    @Test
    @DisplayName("opendataRestTemplate 빈은 HttpComponentsClientHttpRequestFactory 기반으로 생성된다")
    void opendataRestTemplate_usesHttpComponentsRequestFactory() {
        RestTemplate restTemplate = config.opendataRestTemplate();

        assertThat(restTemplate).isNotNull();
        assertThat(restTemplate.getRequestFactory()).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
    }

    @Test
    @DisplayName("restTemplate과 opendataRestTemplate은 서로 독립된 인스턴스와 요청 팩토리를 가진다")
    void twoRestTemplates_areIndependentInstances() {
        RestTemplate restTemplate = config.restTemplate();
        RestTemplate opendataRestTemplate = config.opendataRestTemplate();

        assertThat(restTemplate).isNotSameAs(opendataRestTemplate);

        ClientHttpRequestFactory factoryA = restTemplate.getRequestFactory();
        ClientHttpRequestFactory factoryB = opendataRestTemplate.getRequestFactory();
        assertThat(factoryA).isNotSameAs(factoryB);
    }

    @Test
    @DisplayName("빈 팩토리 메서드를 다시 호출하면 매번 새 RestTemplate이 생성된다")
    void restTemplate_returnsNewInstanceOnEachCall() {
        RestTemplate first = config.restTemplate();
        RestTemplate second = config.restTemplate();

        assertThat(first).isNotSameAs(second);
    }
}