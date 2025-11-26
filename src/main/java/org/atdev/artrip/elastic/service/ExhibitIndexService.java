package org.atdev.artrip.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.elastic.document.KeywordInfo;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.elastic.document.ExhibitDocument;
import org.atdev.artrip.elastic.repository.ExhibitDocumentRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExhibitIndexService {

    private final ExhibitRepository exhibitRepository;
    private final ExhibitDocumentRepository exhibitDocumentRepository;

    private ExhibitDocument convertToDocument(Exhibit exhibit) {
        List<KeywordInfo> keywordInfos = exhibit.getKeywords().stream()
                .map(keyword -> KeywordInfo.builder()
                        .name(keyword.getName())
                        .type(keyword.getType())
                        .build())
                .collect(Collectors.toList());

        return ExhibitDocument.builder()
                .id(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .description(exhibit.getDescription())
                .startDate(exhibit.getStartDate())
                .endDate(exhibit.getEndDate())
                .status(exhibit.getStatus())
                .posterUrl(exhibit.getPosterUrl())
                .ticketUrl(exhibit.getTicketUrl())
                .latitude(exhibit.getLatitude())
                .longitude(exhibit.getLongitude())
                .keywords(keywordInfos)
                .build();
    }

    @Transactional
    public int indexAllExhibits(){
        try{
            List<Exhibit> exhibits = exhibitRepository.findAllWithKeywords();
            int count = exhibits.size();

            if (exhibits.isEmpty()) {
                log.warn("No exhibits found");
                return 0;
            }

            List<ExhibitDocument> documents = exhibits.stream()
                    .map(this::convertToDocument)
                    .collect(Collectors.toList());

            exhibitDocumentRepository.saveAll(documents);

            return count;

        } catch (Exception e) {
            log.error("Elasticsearch indexing error: {}", e.getClass().getName(), e);
            throw new GeneralException(CommonError._INTERNAL_SERVER_ERROR);
        }
    }

    public void indexExhibit(Exhibit exhibit){
        try {
            ExhibitDocument doc = convertToDocument(exhibit);
            exhibitDocumentRepository.save(doc);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(CommonError._BAD_REQUEST);
        }
    }

    public void deleteExhibit(Long exhibitId){
        try {
            exhibitDocumentRepository.deleteById(exhibitId);
            log.debug("Deleted exhibit: {}", exhibitId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(CommonError._BAD_REQUEST);
        }
    }


}
