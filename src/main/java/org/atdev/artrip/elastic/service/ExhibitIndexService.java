package org.atdev.artrip.elastic.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.analysis.TokenChar;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.elastic.document.KeywordInfo;
import org.atdev.artrip.elastic.document.ExhibitDocument;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.ElasticError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExhibitIndexService {

    private final ElasticsearchClient esClient;
    private final ExhibitRepository exhibitRepository;
    private final String EXHIBIT_INDEX = "exhibits";

    private ExhibitDocument convertToDocument(Exhibit exhibit) {

        List<KeywordInfo> keywordInfos = exhibit.getKeywords().stream()
                .map(keyword -> KeywordInfo.builder()
                        .name(keyword.getName())
                        .type(keyword.getType())
                        .build())
                .collect(Collectors.toList());

        ExhibitDocument.ExhibitDocumentBuilder builder = ExhibitDocument.builder()
                .id(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .description(exhibit.getDescription())
                .startDate(exhibit.getStartDate())
                .endDate(exhibit.getEndDate())
                .status(exhibit.getStatus())
                .posterUrl(exhibit.getPosterUrl())
                .ticketUrl(exhibit.getTicketUrl())
                .latitude(exhibit.getExhibitHall().getLatitude())
                .longitude(exhibit.getExhibitHall().getLongitude())
                .keywords(keywordInfos);

        return builder.build();
    }

    public void createAndApplyIndex() {
        try {
            if (esClient.indices().exists(r -> r.index(EXHIBIT_INDEX)).value()) {
                esClient.indices().delete(r -> r.index(EXHIBIT_INDEX));
                log.info("Existing index deleted : {}", EXHIBIT_INDEX);
            }

            esClient.indices().create(c -> c
                            .index(EXHIBIT_INDEX)
                            .settings(s -> s
                                    .maxNgramDiff(9)
                                    .analysis(a -> a
                                            .tokenizer("edge_ngram_tokenizer", t -> t
                                                    .definition(d -> d
                                                            .edgeNgram(en -> en
                                                                    .minGram(1)
                                                                    .maxGram(10)
                                                                    .tokenChars(TokenChar.Letter, TokenChar.Digit)
                                                            )
                                                    )
                                            )
                                            .filter("ngram_filter", f -> f
                                                    .definition(d -> d
                                                            .ngram(ng -> ng
                                                                    .minGram(1)
                                                                    .maxGram(10)
                                                            )
                                                    )
                                            )
                                            .analyzer("edge_ngram_analyzer", an -> an
                                                    .custom(ca -> ca
                                                            .tokenizer("edge_ngram_tokenizer")
                                                            .filter("lowercase")
                                                    )
                                            )
                                            .analyzer("ngram_nori_analyzer", an -> an
                                                    .custom(ca -> ca
                                                            .tokenizer("nori_tokenizer")
                                                            .filter("lowercase", "ngram_filter")
                                                    )
                                            )
                                    )
                            )
                            .mappings(m -> m
                                    .properties("title", p -> p
                                            .text(t -> t
                                                    .analyzer("edge_ngram_analyzer")
                                                    .searchAnalyzer("standard")
                                                    .fields("nori", f -> f.text(t2 -> t2.analyzer("ngram_nori_analyzer")))
                                                    .fields("keyword", f -> f.keyword(k -> k))))
                                    .properties("description", p -> p.text(t -> t
                                            .analyzer("edge_ngram_analyzer")
                                            .searchAnalyzer("standard")
                                            .fields("nori", f -> f.text(t2 -> t2.analyzer("ngram_nori_analyzer")))))
                                    .properties("startDate", p -> p.date(d -> d.format("yyyy.MM.dd")))
                                    .properties("endDate", p -> p.date(d -> d.format("yyyy.MM.dd")))
                                    .properties("status", p -> p.keyword(k -> k))
                                    .properties("posterUrl", p -> p.keyword(k -> k))
                                    .properties("ticketUrl", p -> p.keyword(k -> k))
                                    .properties("keywords", p -> p
                                            .nested(n -> n
                                                    .properties("name", np -> np
                                                            .text(t -> t
                                                                    .analyzer("edge_ngram_analyzer")
                                                                    .searchAnalyzer("standard")
                                                                    .fields("nori", f -> f.text(t2 -> t2.analyzer("ngram_nori_analyzer")))
                                                                    .fields("keyword", f -> f.keyword(k -> k))
                                                            )
                                                    )
                                                    .properties("type", np -> np.keyword(k -> k))
                                            )
                                    )
                            )
            );

            log.info("index '{}' created with Nori And Edge Ngram analyzer", EXHIBIT_INDEX);
        } catch (IOException e) {
            log.error("Error createing or applying index settings", e);
            if (e.getMessage().contains("nori_tokenizer")) {
                throw new GeneralException(ElasticError._ES_ANALYZER_CONFIG_FAILED);
            }
            throw new GeneralException(ElasticError._ES_INDEX_CREATE_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public int indexAllExhibits() {
        try {
            List<Exhibit> exhibits = exhibitRepository.findAllWithKeywords();
            int count = exhibits.size();

            if (exhibits.isEmpty()) {
                log.warn("No exhibits found");
                return 0;
            }

            List<ExhibitDocument> documents = exhibits.stream()
                    .map(this::convertToDocument)
                    .collect(Collectors.toList());

            BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
            for (ExhibitDocument doc : documents) {
                bulkBuilder.operations(op -> op
                        .index(idx -> idx
                                .index(EXHIBIT_INDEX)
                                .id(String.valueOf(doc.getId()))
                                .document(doc)));
            }
            BulkResponse response = esClient.bulk(bulkBuilder.build());

            if (response.errors()) {
                log.error("Bulk indexing failed with errors : {} items failed" , response.items().stream().filter(i -> i.error() != null).count());
                response.items().stream()
                        .filter(i -> i.error() != null)
                        .limit(5)
                        .forEach(i -> log.error("item error : {}", i.error().reason()));

            }
            if (response.errors()) {
                throw new GeneralException(ElasticError._ES_BULK_PARTIAL_FAILED);
            }
            return count;
        } catch (Exception e) {
            log.error("Elasticsearch indexing error : {}", e.getClass().getName(), e );
            throw new GeneralException(ElasticError._ES_BULK_INDEX_FAILED);
        }
    }

    @Transactional
    public void indexExhibit(Exhibit exhibit) {
        try {
            ExhibitDocument doc = convertToDocument(exhibit);
            esClient.index(i -> i
                    .index(EXHIBIT_INDEX)
                    .id(String.valueOf(doc.getId()))
                    .document(doc));
        } catch (Exception e) {
            log.error("Indexing failed : {}", e.getMessage(), e);
            throw new GeneralException(CommonError._INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteExhibit(Long exhibitId) {
        try {
            esClient.delete(d -> d
                    .index(EXHIBIT_INDEX)
                    .id(String.valueOf(exhibitId)));
        } catch (Exception e) {
            log.error("Delete Failed: {}", e.getMessage(), e);
            throw new GeneralException(CommonError._INTERNAL_SERVER_ERROR);
        }
    }
}
