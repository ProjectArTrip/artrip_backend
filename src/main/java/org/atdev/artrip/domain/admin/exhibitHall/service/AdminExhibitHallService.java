package org.atdev.artrip.domain.admin.exhibitHall.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.admin.common.dto.Criteria;
import org.atdev.artrip.domain.admin.common.dto.PagingResponseDTO;
import org.atdev.artrip.domain.admin.exhibitHall.converter.AdminExhibitHallConverter;
import org.atdev.artrip.domain.admin.exhibitHall.dto.request.CreateExhibitHallRequest;
import org.atdev.artrip.domain.admin.exhibitHall.dto.response.ExhibitHallListResponse;
import org.atdev.artrip.domain.admin.exhibitHall.dto.response.ExhibitHallResponse;
import org.atdev.artrip.domain.admin.exhibitHall.dto.request.UpdateExhibitHallRequest;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.atdev.artrip.domain.exhibitHall.repository.ExhibitHallRepository;
import org.atdev.artrip.global.apipayload.code.status.ExhibitError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminExhibitHallService {

    private final ExhibitHallRepository exhibitHallRepository;
    private final ExhibitRepository exhibitRepository;
    private final AdminExhibitHallConverter exhibitHallConverter;

    @Transactional(readOnly = true)
    public PagingResponseDTO<ExhibitHallListResponse> getExhibitHallList(Criteria cri) {
        log.info("Admin getting exhibit hall list: {}", cri);

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
        log.info("Admin getting exhibit hall : {}", exhibitHallId);

        ExhibitHall hall = exhibitHallRepository.findById(exhibitHallId).orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_HALL_NOT_FOUND));

        long exhibitCount = exhibitRepository.countByExhibitHall_ExhibitHallId(exhibitHallId);

        return exhibitHallConverter.toResponse(hall, exhibitCount);
    }

    @Transactional
    public Long createExhibitHall(CreateExhibitHallRequest request) {
        log.info("Admin creating exhibit hall: {}", request);

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

        log.info("Exhibit hall saved: {}", savedHall);

        return savedHall.getExhibitHallId();
    }

    @Transactional
    public Long updateExhibitHall(Long exhibitHallId, UpdateExhibitHallRequest request) {
        log.info("Admin updating exhibit hall: {}, {}", exhibitHallId, request);

        ExhibitHall hall = exhibitHallRepository.findById(exhibitHallId).orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        if (request.getName() != null) {hall.setName(request.getName());}
        if (request.getAddress() != null) {hall.setAddress(request.getAddress());}
        if (request.getCountry() != null) {hall.setCountry(request.getCountry());}
        if (request.getRegion() != null) {hall.setRegion(request.getRegion());}
        if (request.getPhone() != null) {hall.setPhone(request.getPhone());}
        if (request.getHomepageUrl() != null) {hall.setHomepageUrl(request.getHomepageUrl());}
        if (request.getOpeningHours() != null) {hall.setOpeningHours(request.getOpeningHours());}
        if (request.getIsDomestic() != null) {hall.setIsDomestic(request.getIsDomestic());}
        if (request.getClosedDays() != null) {hall.setClosedDays(request.getClosedDays());}

        hall.setUpdatedAt(LocalDateTime.now());
        ExhibitHall savedHall = exhibitHallRepository.save(hall);

        log.info("ExhibitHall updated: id={}, name={}", savedHall.getExhibitHallId(), savedHall.getName());

        return savedHall.getExhibitHallId();
    }

    @Transactional
    public void deleteExhibitHall(Long exhibitHallId) {
        log.info("Admin deleting exhibit hall: {}", exhibitHallId);

        if (!exhibitHallRepository.existsById(exhibitHallId)) {
            throw  new GeneralException(ExhibitError._EXHIBIT_HALL_NOT_FOUND);
        }

        long exhibitCount = exhibitRepository.countByExhibitHall_ExhibitHallId(exhibitHallId);
        if (exhibitCount > 0) {
            throw new GeneralException(ExhibitError._EXHIBIT_HALL_IN_USE);
        }

        exhibitHallRepository.deleteById(exhibitHallId);

        log.info("Exhibit hall deleted: {}", exhibitHallId);
    }


}
