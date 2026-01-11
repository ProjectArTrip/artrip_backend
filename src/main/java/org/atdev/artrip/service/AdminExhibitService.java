package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.page.Criteria;
import org.atdev.artrip.global.page.PagingResponseDTO;
import org.atdev.artrip.converter.AdminExhibitConverter;
import org.atdev.artrip.controller.dto.request.CreateExhibitRequest;
import org.atdev.artrip.controller.dto.response.AdminExhibitResponse;
import org.atdev.artrip.controller.dto.response.AdminExhibitListResponse;
import org.atdev.artrip.controller.dto.request.UpdateExhibitRequest;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.repository.ExhibitHallRepository;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.repository.KeywordRepository;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.ExhibitError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final ExhibitHallRepository exhibitHallRepository;
    private final KeywordRepository keywordRepository;
    private final AdminExhibitConverter adminExhibitConverter;

    @Transactional(readOnly = true)
    public PagingResponseDTO<AdminExhibitListResponse> getExhibitList(Criteria cri) {

        Pageable pageable = cri.toPageable();

        Page<Exhibit> exhibitPage;

        if (cri.getSearchValue() != null && !cri.getSearchValue().isEmpty()) {
            exhibitPage = exhibitRepository.findByDescriptionContaining(cri.getSearchValue(), pageable);
        } else {
            exhibitPage = exhibitRepository.findAll(pageable);
        }

        Page<AdminExhibitListResponse> responsePage = exhibitPage.map(adminExhibitConverter::toListResponse);

        return PagingResponseDTO.from(responsePage);
    }

    @Transactional(readOnly = true)
    public AdminExhibitResponse getExhibit (Long exhibitId) {

        Exhibit exhibit = exhibitRepository.findByIdWithKeywords(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        return adminExhibitConverter.toAdminResponse(exhibit);
    }

    @Transactional
    public Long createExhibit(CreateExhibitRequest request) {

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

        try {
        } catch (Exception e) {
        }
        return null;
    }

    @Transactional
    public Long updateExhibit(Long exhibitId, UpdateExhibitRequest request) {

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

        // TODO: 업데이트 할 내용 추가
        try {

        }catch (Exception e) {
            throw new GeneralException(CommonError._INTERNAL_SERVER_ERROR);
        }

        return savedExhibit.getExhibitId();
    }

    @Transactional
    public void deleteExhibit(Long exhibitId) {

        if (!exhibitRepository.existsById(exhibitId)) {
            throw new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND);
        }

        exhibitRepository.deleteById(exhibitId);

        // TODO: delete 할 code 추가
        try {
            System.out.println("delete Exhibit code");
        } catch (Exception e) {
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
                    .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_HALL_NOT_FOUND));
        } else if (exhibitHallName != null) {
            ExhibitHall hall = ExhibitHall.builder()
                    .name(exhibitHallName)
                    .address(address)
                    .country(country)
                    .region(region)
                    .phone(phone)
                    .openingHours(openingHours)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return exhibitHallRepository.save(hall);
        } else {
            throw new GeneralException(CommonError._BAD_REQUEST);
        }
    }


}
