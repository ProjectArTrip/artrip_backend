package org.atdev.artrip.external.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.external.publicdata.properties.PublicDataProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient publicDataWebClient(PublicDataProperties properties) {

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout())
                .responseTimeout(Duration.ofMillis(properties.getReadTimeout()))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(properties.getReadTimeout(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(properties.getReadTimeout(), TimeUnit.MILLISECONDS)));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().maxInMemorySize(10 * 1024 * 1024);
                    config.customCodecs().register(
                            new Jackson2JsonDecoder(xmlMapper,
                                    MediaType.TEXT_XML,
                                    MediaType.APPLICATION_XML,
                                    new MediaType("text", "xml", StandardCharsets.UTF_8))
                    );
                    config.customCodecs().register(
                            new Jackson2JsonEncoder(xmlMapper,
                                    MediaType.TEXT_XML,
                                    MediaType.APPLICATION_XML
                            )
                    );
                }).build();

        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .defaultHeader("Accept", MediaType.APPLICATION_XML_VALUE + ", " + MediaType.TEXT_XML_VALUE + ", */*")
                .defaultHeader("User-Agent", "ArtTrip/1.0")
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logResponse () {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("공공데이터 API 응답 Status : {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });

    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("[공공데이터 API 요청] {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }
}
