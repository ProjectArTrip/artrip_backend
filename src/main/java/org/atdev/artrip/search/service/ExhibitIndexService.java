package org.atdev.artrip.search.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.search.document.ExhibitDocument;
import org.atdev.artrip.search.dto.ExhibitResponse;
import org.atdev.artrip.search.repository.ExhibitSearchRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.search.repository.ExhibitRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExhibitIndexService {

    private final ExhibitRepository exhibitRepository;
    private final ExhibitSearchRepository exhibitSearchRepository;

    public void indexAllExhibits(){
        List<Exhibit> exhibits = exhibitRepository.findAll();

        List<ExhibitDocument> docs = exhibits.stream().map(e -> {
            ExhibitDocument doc = new ExhibitDocument();
            doc.setId(e.getExhibitId());
            doc.setTitle(e.getTitle());
            doc.setDescription(e.getDescription());
            doc.setStartDate(e.getStartDate() != null ? e.getStartDate().toInstant(ZoneOffset.UTC).toEpochMilli() : null);
            doc.setEndDate(e.getEndDate() != null ? e.getEndDate().toInstant(ZoneOffset.UTC).toEpochMilli() : null);
            doc.setStatus(e.getStatus());
            doc.setPosterUrl(e.getPosterUrl());
            doc.setTicketUrl(e.getTicketUrl());
            doc.setGenre(e.getGenre());
            doc.setLatitude(e.getLatitude());
            doc.setLongitude(e.getLongitude());

            return doc;
        }).collect(Collectors.toList());

        exhibitSearchRepository.saveAll(docs);
    }

    public List<ExhibitResponse> searchExhibits(String keyword){
        ZoneId zone = ZoneId.systemDefault();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        List<ExhibitDocument> docs = exhibitSearchRepository.findByTitleContaining(keyword);

        return docs.stream()
                .map(doc -> ExhibitResponse.builder()
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
