package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.page.Criteria;
import org.atdev.artrip.global.page.PagingResponseDTO;
import org.atdev.artrip.converter.AdminExhibitHallConverter;
import org.atdev.artrip.controller.dto.request.CreateExhibitHallRequest;
import org.atdev.artrip.controller.dto.response.ExhibitHallListResponse;
import org.atdev.artrip.controller.dto.response.ExhibitHallResponse;
import org.atdev.artrip.controller.dto.request.UpdateExhibitHallRequest;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.repository.ExhibitHallRepository;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminExhibitHallService {

    private final ExhibitHallRepository exhibitHallRepository;
    private final ExhibitRepository exhibitRepository;
    private final AdminExhibitHallConverter exhibitHallConverter;

    @Transactional(readOnly = true)
    public PagingResponseDTO<ExhibitHallListResponse> getExhibitHallList(Criteria cri) {

        Pageable pageable = cri.toPageable();
        Page<ExhibitHall> hallPage;

        if(cri.getSearchValue() != null && !cri.getSearchValue().isEmpty()) {
            hallPage = exhibitHallRepository.findByNameContaining(cri.getSearchValue(), pageable);
        } else {
            hallPage = exhibitHallRepository.findAll(pageable);
        }

        Page<ExhibitHallListResponse> responsePage = hallPage.map(hall ->{
            long exhibitCount = exhibitRepository.countByExhibitHall_ExhibitHallId(hall.getExhibitHallId());
                return exhibitHallConverter.toListResponse(hall, exhibitCount);
                });

        return PagingResponseDTO.from(responsePage);

    }

    @Transactional(readOnly = true)
    public ExhibitHallResponse getExhibitHall(Long exhibitHallId) {

        ExhibitHall hall = exhibitHallRepository.findById(exhibitHallId).orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_HALL_NOT_FOUND));

        long exhibitCount = exhibitRepository.countByExhibitHall_ExhibitHallId(exhibitHallId);

        return exhibitHallConverter.toResponse(hall, exhibitCount);
    }

    @Transactional
    public Long createExhibitHall(CreateExhibitHallRequest request) {

        ExhibitHall exhibitHall = ExhibitHall.builder()
                .name(request.getName())
                .address(request.getAddress())
                .country(request.getCountry())
                .region(request.getRegion())
                .phone(request.getPhone())
                .homepageUrl(request.getHomepageUrl())
                .openingHours(request.getOpeningHours())
                .closedDays(request.getClosedDays())
                .isDomestic(request.getIsDomestic())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ExhibitHall savedHall = exhibitHallRepository.save(exhibitHall);

        return savedHall.getExhibitHallId();
    }

    @Transactional
    public Long updateExhibitHall(Long exhibitHallId, UpdateExhibitHallRequest request) {

    return null;
    }

    @Transactional
    public void deleteExhibitHall(Long exhibitHallId) {

        if (!exhibitHallRepository.existsById(exhibitHallId)) {
            throw  new GeneralException(ExhibitErrorCode._EXHIBIT_HALL_NOT_FOUND);
        }

        long exhibitCount = exhibitRepository.countByExhibitHall_ExhibitHallId(exhibitHallId);
        if (exhibitCount > 0) {
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_HALL_IN_USE);
        }

        exhibitHallRepository.deleteById(exhibitHallId);

    }


}
