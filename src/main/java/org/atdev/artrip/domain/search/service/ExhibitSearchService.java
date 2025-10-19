package org.atdev.artrip.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.search.response.ExhibitSearchResponse;
import org.atdev.artrip.elastic.document.ExhibitDocument;
import org.atdev.artrip.elastic.repository.ExhibitDocumentRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExhibitSearchService {

    private final ExhibitDocumentRepository exhibitDocumentRepository;
    private final SearchHistoryService searchHistoryService;

    public List<ExhibitSearchResponse> search(String keyword, Long userId) {
        log.info("Searching for exhibits for user= {}, keyword= {}", userId, keyword);

        List<ExhibitDocument> docs = exhibitDocumentRepository.findByTitleContaining(keyword);
        log.info("Found {} exhibits for user= {}", docs.size(), userId);

        if (userId != null && !keyword.isBlank()) {
            try {
                searchHistoryService.create(userId, keyword);
            } catch (Exception e) {
                log.error("Error saving search history for user= {}, keyword= {}", userId, keyword, e);
            }
        }
        return docs.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ExhibitSearchResponse convertToResponse(ExhibitDocument doc) {
        DateTimeFormatter ftf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return ExhibitSearchResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .startDate(doc.getStartDate() != null ? doc.getStartDate().format(ftf) : null)
                .endDate(doc.getEndDate() != null ? doc.getEndDate().format(ftf) : null)
                .status(doc.getStatus())
                .posterUrl(doc.getPosterUrl())
                .ticketUrl(doc.getTicketUrl())
//                .genre(doc.getGenre())
                .latitude(doc.getLatitude())
                .longitude(doc.getLongitude())
                .build();
    }


}
