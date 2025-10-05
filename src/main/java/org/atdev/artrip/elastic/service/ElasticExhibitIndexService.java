package org.atdev.artrip.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.apipayload.code.status.ErrorStatus;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.elastic.document.ElasticDocument;
import org.atdev.artrip.elastic.dto.EsSearchResponse;
import org.atdev.artrip.elastic.repository.ElasticExhibitSearchRepository;
import org.atdev.artrip.domain.Exhibit;
import org.atdev.artrip.elastic.repository.EsExhibitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
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
        doc.setStartDate(exhibit.getStartDate() != null ? exhibit.getStartDate().getTime() : null);
        doc.setEndDate(exhibit.getEndDate() != null ? exhibit.getEndDate().getTime() : null);
        doc.setStatus(exhibit.getStatus() != null ? exhibit.getStatus().toString() : null);
        doc.setPosterUrl(exhibit.getPosterUrl());
        doc.setTicketUrl(exhibit.getTicketUrl());
        doc.setGenre(exhibit.getGenre() != null ? exhibit.getGenre().toString() : null);
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
            log.error(e.getMessage(), e);
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
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

    public List<EsSearchResponse> searchExhibits(String keyword){
        ZoneId zone = ZoneId.systemDefault();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        List<ElasticDocument> docs = elasticExhibitSearchRepository.findByTitleContaining(keyword);

        return docs.stream()
                .map(doc -> EsSearchResponse.builder()
                        .id(doc.getId())
                        .title(doc.getTitle())
                        .description(doc.getDescription())
                        .startDate(doc.getStartDate() == null ? null :
                                Instant.ofEpochMilli(doc.getStartDate())
                                        .atZone(zone)
                                        .toLocalDateTime()
                                        .format(fmt))
                        .endDate(doc.getEndDate() == null ? null :
                                Instant.ofEpochMilli(doc.getEndDate())
                                        .atZone(zone)
                                        .toLocalDateTime()
                                        .format(fmt))
                        .status(doc.getStatus())
                        .posterUrl(doc.getPosterUrl())
                        .ticketUrl(doc.getTicketUrl())
                        .genre(doc.getGenre())
                        .build())
                .toList();
    }

}
