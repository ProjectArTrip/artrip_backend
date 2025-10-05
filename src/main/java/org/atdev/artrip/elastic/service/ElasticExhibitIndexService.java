package org.atdev.artrip.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.apipayload.code.status.ErrorStatus;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.elastic.document.ElasticDocument;
import org.atdev.artrip.elastic.dto.EsSearchResponse;
import org.atdev.artrip.elastic.repository.ElasticExhibitSearchRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.elastic.repository.EsExhibitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticExhibitIndexService {

    private final EsExhibitRepository elasticExhibitRepository;
    private final ElasticExhibitSearchRepository elasticExhibitSearchRepository;

    private ElasticDocument convertToDocument(Exhibit exhibit) {
        ElasticDocument doc = new ElasticDocument();
        doc.setId(exhibit.getExhibitId());
        doc.setTitle(exhibit.getTitle());
        doc.setDescription(exhibit.getDescription());
        doc.setStartDate(exhibit.getStartDate());
        doc.setEndDate(exhibit.getEndDate());
        doc.setStatus(exhibit.getStatus());
        doc.setPosterUrl(exhibit.getPosterUrl());
        doc.setTicketUrl(exhibit.getTicketUrl());
        doc.setGenre(exhibit.getGenre());
        doc.setLatitude(exhibit.getLatitude());
        doc.setLongitude(exhibit.getLongitude());
        return doc;
    }

    @Transactional
    public int indexAllExhibits(){
        try{
            List<Exhibit> exhibits = elasticExhibitRepository.findAll();
            int count = exhibits.size();

            if (exhibits.isEmpty()) {
                log.warn("No exhibits found");
                return 0;
            }

            List<ElasticDocument> documents = exhibits.stream()
                    .map(this::convertToDocument)
                    .collect(Collectors.toList());

            elasticExhibitSearchRepository.saveAll(documents);

            return count;
        } catch (Exception e) {
            log.error("Elasticsearch indexing error: {}", e.getClass().getName(), e);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public void indexExhibit(Exhibit exhibit){
        try {
            ElasticDocument doc = convertToDocument(exhibit);
            elasticExhibitSearchRepository.save(doc);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }

    public List<EsSearchResponse> searchExhibits(String keyword) {

        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        List<ElasticDocument> docs = elasticExhibitSearchRepository.findByTitleContaining(keyword);

        return docs.stream()
                .map(doc -> EsSearchResponse.builder()
                        .id(doc.getId())
                        .title(doc.getTitle())
                        .description(doc.getDescription())
                        // ⭐ 이미 LocalDateTime이므로 그냥 format만 하면 됨
                        .startDate(doc.getStartDate() == null ? null :
                                doc.getStartDate().format(fmt))
                        .endDate(doc.getEndDate() == null ? null :
                                doc.getEndDate().format(fmt))
                        .status(doc.getStatus())
                        .posterUrl(doc.getPosterUrl())
                        .ticketUrl(doc.getTicketUrl())
                        .genre(doc.getGenre())
                        .build())
                .toList();
    }

}
