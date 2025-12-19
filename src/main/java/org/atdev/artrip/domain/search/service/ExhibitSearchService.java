package org.atdev.artrip.domain.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.search.web.dto.response.ExhibitSearchResponse;
import org.atdev.artrip.elastic.document.ExhibitDocument;
import org.atdev.artrip.global.apipayload.code.status.SearchError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExhibitSearchService {

    private final ElasticsearchClient esClient;
    private final SearchHistoryService searchHistoryService;

    private final static String EXHIBIT_INDEX = "exhibits";

    public List<ExhibitSearchResponse> searchExhibits(String keyword, Long userId) {
        log.info("Searching exhibits - user: {}, keyword: {}", userId, keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new GeneralException(SearchError._SEARCH_KEYWORD_EMPTY);
        }

        // 글자 길이 검증 로직 필요 시 활성화 예정
//        keyword = keyword.trim();
//
//        if (keyword.length() < 2) {
//            throw new GeneralException(SearchError._SEARCH_KEYWORD_TOO_SHORT);
//        }
//
//        if (keyword.length() > 50) {
//            throw new GeneralException(SearchError._SEARCH_KEYWORD_TOO_LONG);
//        }
        if (userId != null) {
            try {
                searchHistoryService.create(userId, keyword);
            }catch (Exception e) {
                log.error("Error Saving search history", e);
            }
        }

        try {
            Query multiMatchQuery = Query.of(q -> q
                    .multiMatch(m -> m
                            .query(keyword)
                            .fields(
                                    "title^3",
                                    "title.nori^2",
                                    "description^1",
                                    "description.nori^1"
                            )
                            .fuzziness("AUTO")
                    )
            );
            Query nestedKeywordQuery = Query.of(q -> q
                    .nested(n -> n
                            .path("keywords")
                            .query(nq -> nq
                                    .multiMatch(m -> m
                                            .query(keyword)
                                            .fields(
                                                    "keywords.name^3",
                                                    "keywords.name.nori^2"
                                            )
                                            .fuzziness("AUTO")
                                    )
                            )
                    )
            );

            Query combinedQuery = Query.of(q -> q
                    .bool(b -> b
                            .should(multiMatchQuery)
                            .should(nestedKeywordQuery)
                            .minimumShouldMatch("1")
                    )
            );

            SearchRequest searchRequest = SearchRequest.of(r -> r
                    .index(EXHIBIT_INDEX)
                    .query(combinedQuery)
                    .size(100)
                    .sort(s -> s.score(sc -> sc.order(SortOrder.Desc)))
            );

            SearchResponse<ExhibitDocument> response = esClient.search(searchRequest, ExhibitDocument.class);

            List<ExhibitSearchResponse> results = response.hits().hits().stream()
                    .map(Hit::source)
                    .map(this::convertToExhibitResponse)
                    .collect(Collectors.toList());

            return results;

        } catch (IOException e) {
            log.error("Elasticsearch search error", e);
            throw new GeneralException(SearchError._SEARCH_EXHIBIT_NOT_FOUND);
        }
    }

    private ExhibitSearchResponse convertToExhibitResponse(ExhibitDocument doc) {
        DateTimeFormatter ftt = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return ExhibitSearchResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .startDate(doc.getStartDate() != null ? doc.getStartDate().format(ftt) : null)
                .endDate(doc.getEndDate() != null ? doc.getEndDate().format(ftt) : null)
                .status(doc.getStatus())
                .posterUrl(doc.getPosterUrl())
                .ticketUrl(doc.getTicketUrl())
                .keywords(doc.getKeywords())
                .latitude(doc.getLatitude())
                .longitude(doc.getLongitude())
                .build();
    }

}
