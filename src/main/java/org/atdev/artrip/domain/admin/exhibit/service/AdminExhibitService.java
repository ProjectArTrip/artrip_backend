package org.atdev.artrip.domain.admin.exhibit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.admin.common.dto.Criteria;
import org.atdev.artrip.domain.admin.common.dto.PagingResponseDTO;
import org.atdev.artrip.domain.admin.exhibit.dto.CreateExhibitRequest;
import org.atdev.artrip.domain.admin.exhibit.dto.ExhibitAdminResponse;
import org.atdev.artrip.domain.admin.exhibit.dto.ExhibitListResponse;
import org.atdev.artrip.domain.admin.exhibit.dto.UpdateExhibitRequest;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.atdev.artrip.domain.exhibitHall.repository.ExhibitHallRepository;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.repository.KeywordRepository;
import org.atdev.artrip.elastic.service.ExhibitIndexService;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.ExhibitError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final ExhibitHallRepository exhibitHallRepository;
    private final KeywordRepository keywordRepository;
    private final ExhibitIndexService exhibitIndexService;

    @Transactional
    public PagingResponseDTO<ExhibitListResponse> getExhibitList(Criteria cri) {
        log.info("Admin Getting Exhibit List : {}", cri);

        Pageable pageable = cri.toPageable();

        Page<Exhibit> exhibitPage;

        if (cri.getSearchValue() != null && !cri.getSearchValue().isEmpty()) {
            exhibitPage = exhibitRepository.findByDescriptionContaining(cri.getSearchValue(), pageable);
        } else {
            exhibitPage = exhibitRepository.findAll(pageable);
        }

        Page<ExhibitListResponse> responsePage = exhibitPage.map(this::convertToListResponse);

        return PagingResponseDTO.from(responsePage);
    }

    @Transactional
    public ExhibitAdminResponse getExhibit (Long exhibitId) {
        log.info("Admin Getting Exhibit : {}", exhibitId);

        Exhibit exhibit = exhibitRepository.findByIdWithKeywords(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        return convertToAdminResponse(exhibit);
    }


    @Transactional
    public Long createExhibit(CreateExhibitRequest request) {
        log.info("Admin Creating exhibit : title={}", request);

        ExhibitHall exhibitHall = exhibitHallRepository.findById(request.getExhibitHallId())
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_HALL_NOT_FOUND));

        List<Keyword> keywords = List.of();
        if (request.getKeywordIds() != null && !request.getKeywordIds().isEmpty()) {
            keywords = keywordRepository.findAllById(request.getKeywordIds());
            if (keywords.size() != request.getKeywordIds().size()) {
                throw new GeneralException(CommonError._BAD_REQUEST);
            }
        }

        Exhibit exhibit = Exhibit.builder()
                .exhibitHall(exhibitHall)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .ticketUrl(request.getTicketUrl())
                .posterUrl(request.getPosterUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        exhibit.getKeywords().addAll(keywords);

        Exhibit savedExhibit = exhibitRepository.save(exhibit);

        log.info("Exhibit created : id={}, keywords.size={} ",
                savedExhibit.getExhibitId(),
                savedExhibit.getKeywords().size());

        try {
            exhibitIndexService.indexExhibit(savedExhibit);
            log.info("Exhibit indexing failed : id={}, error={}",savedExhibit.getExhibitId());
        } catch (Exception e) {
            log.error("Exhibit indexing failed", e);
        }
        return savedExhibit.getExhibitId();
    }

    @Transactional
    public Long updateExhibit(Long exhibitId, UpdateExhibitRequest request) {
        log.info("Admin Updating Exhibit : {}", exhibitId);

        Exhibit exhibit = exhibitRepository.findByIdWithKeywords(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        if (request.getExhibitHallId() != null || request.getExhibitHallName() != null ) {
            ExhibitHall exhibitHall = getOrCreateExhibitHall(
                    request.getExhibitHallId(),
                    request.getExhibitHallName(),
                    request.getAddress(),
                    request.getCountry(),
                    request.getRegion(),
                    request.getPhone(),
                    request.getOpeningHours()
            );
            exhibit.setExhibitHall(exhibitHall);
        }

        if (request.getTitle() != null) exhibit.setTitle(request.getTitle());
        if (request.getDescription() != null) exhibit.setDescription(request.getDescription());
        if (request.getStartDate() != null) exhibit.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) exhibit.setEndDate(request.getEndDate());
        if (request.getStatus() != null) exhibit.setStatus(request.getStatus());
        if (request.getTicketUrl() != null) exhibit.setTicketUrl(request.getTicketUrl());
        if (request.getPosterUrl() != null) exhibit.setPosterUrl(request.getPosterUrl());

        exhibit.setUpdatedAt(LocalDateTime.now());

        if (request.getKeywordIds() != null) {
            List<Keyword> keywords = keywordRepository.findAllById(request.getKeywordIds());
            exhibit.getKeywords().clear();
            exhibit.getKeywords().addAll(keywords);
        }

        Exhibit savedExhibit = exhibitRepository.save(exhibit);

        try {
            exhibitIndexService.indexExhibit(savedExhibit);


        }catch (Exception e) {
            log.error("Admin Exhibit Update Error", e.getMessage());
            throw new GeneralException(CommonError._INTERNAL_SERVER_ERROR);
        }

        return savedExhibit.getExhibitId();
    }

    @Transactional
    public void deleteExhibit(Long exhibitId) {
        log.info("Admin Deleting Exhibit : {}", exhibitId);

        if (!exhibitRepository.existsById(exhibitId)) {
            throw new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND);
        }

        exhibitRepository.deleteById(exhibitId);

        try {
            exhibitIndexService.deleteExhibit(exhibitId);
        } catch (Exception e) {
        log.error("Admin Exhibit Deletion Error", e.getMessage());
            throw new GeneralException(CommonError._INTERNAL_SERVER_ERROR);
        }
    }

    private ExhibitHall getOrCreateExhibitHall(Long exhibitHallId,
                                                  String exhibitHallName,
                                                    String address,
                                                    String country,
                                                    String region,
                                                    String phone,
                                                    String openingHours) {

        if (exhibitHallId != null) {
            return exhibitHallRepository.findById(exhibitHallId)
                    .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));
        } else if (exhibitHallName != null) {
            ExhibitHall hall = ExhibitHall.builder()
                    .name(exhibitHallName)
                    .address(address)
                    .country(country)
                    .region(region)
                    .phone(phone)
                    .openingHours(openingHours)
                    .build();
            return exhibitHallRepository.save(hall);
        } else {
            throw new GeneralException(CommonError._BAD_REQUEST);
        }
    }

    private ExhibitListResponse convertToListResponse(Exhibit exhibit) {
        return ExhibitListResponse.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .status(exhibit.getStatus())
                .startDate(exhibit.getStartDate())
                .endDate(exhibit.getEndDate())
                .exhibitHallName(exhibit.getExhibitHall() != null ? exhibit.getExhibitHall().getName() : null)
                .country(exhibit.getExhibitHall() != null ? exhibit.getExhibitHall().getCountry() : null)
                .keywordCount(exhibit.getKeywords().size())
                .createdAt(exhibit.getCreatedAt())
                .updatedAt(exhibit.getUpdatedAt())
                .build();
    }

    public ExhibitAdminResponse convertToAdminResponse(Exhibit exhibit) {
        return ExhibitAdminResponse.builder()
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .description(exhibit.getDescription())
                .startDate(exhibit.getStartDate())
                .endDate(exhibit.getEndDate())
                .exhibitHall(convertToExhibitHallInfo(exhibit.getExhibitHall()))
                .openingHours(exhibit.getExhibitHall() != null ? exhibit.getExhibitHall().getOpeningHours() : null)
                .status(exhibit.getStatus())
                .posterUrl(exhibit.getPosterUrl())
                .ticketUrl(exhibit.getTicketUrl())
                .keywords(exhibit.getKeywords().stream()
                        .map(this::convertToKeywordInfo)
                        .collect(Collectors.toList()))
                .createdAt(exhibit.getCreatedAt())
                .updatedAt(exhibit.getUpdatedAt())
                .build();
    }

    private ExhibitAdminResponse.ExhibitHallInfo convertToExhibitHallInfo(ExhibitHall hall) {
        if (hall == null) return null;

        return ExhibitAdminResponse.ExhibitHallInfo.builder()
                .exhibitHallId(hall.getExhibitHallId())
                .name(hall.getName())
                .address(hall.getAddress())
                .country(hall.getCountry())
                .region(hall.getRegion())
                .phone(hall.getPhone())
                .homepageUrl(hall.getHomepageUrl())
                .build();
    }

    private ExhibitAdminResponse.keywordInfo convertToKeywordInfo(Keyword keyword) {
        return ExhibitAdminResponse.keywordInfo.builder()
                .keywordId(keyword.getKeywordId())
                .name(keyword.getName())
                .type(keyword.getType().name())
                .build();
    }
}
