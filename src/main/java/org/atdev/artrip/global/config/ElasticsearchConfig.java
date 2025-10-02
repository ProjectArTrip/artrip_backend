package org.atdev.artrip.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.elastic.service.ElasticExhibitIndexService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchConfig {

    private final ElasticExhibitIndexService indexService;

    @Bean
    @Profile("!test")
    public CommandLineRunner initializeElasticsearchIndex(){
        return args -> {
            log.info("Initializing Elasticsearch index");

            try {
                int indexedCount = indexService.indexAllExhibits();

                log.info("Indexed {} exhibits into Elasticsearch", indexedCount);

            } catch (Exception e) {
                log.error("Error initializing Elasticsearch index", e);
            }
        };
    }
}
