package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Country;
import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.curation.CurationExhibit;
import org.atdev.artrip.global.apipayload.code.error.CurationErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.CurationRepository;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.service.dto.condition.CurationSearchCondition;
import org.atdev.artrip.service.dto.result.CurationDetailCursorResult;
import org.atdev.artrip.service.dto.result.CurationSummaryResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CurationService {

    private final CurationRepository curationRepository;
    private final FavoriteRepository favoriteRepository;
    private static final int SUMMARY_CARD_COUNT = 3;

    @Transactional(readOnly = true)
    public CurationSummaryResult getSummaryCuration(Long userId, CurationSearchCondition condition) {

        LocalDate today = LocalDate.now();

        String country = condition.country();
        Boolean domestic = condition.domestic();
        Boolean domesticFilter = domestic;

        if (country != null) {
            boolean valid = Arrays.stream(Country.values()).anyMatch(c -> c.getLabel().equals(country));
            if (!valid) {
                throw new GeneralException(CurationErrorCode._INVALID_COUNTRY);
            }
        }

        if (Boolean.TRUE.equals(domestic) && country != null) {
            throw new GeneralException(CurationErrorCode._INVALID_LOCATION_FILTER);
        }

        List<Long> candidateIds = curationRepository.findVisibleCurationIds(today, domesticFilter, country, SUMMARY_CARD_COUNT);

        if (candidateIds.isEmpty()) {
            throw new GeneralException(CurationErrorCode._CURATION_NOT_FOUND);
        }

        Long pickedId = candidateIds.get(ThreadLocalRandom.current().nextInt(candidateIds.size()));

        Curation picked = curationRepository.findByIdWithExhibits(pickedId, country).orElseThrow(() -> new GeneralException(CurationErrorCode._CURATION_NOT_FOUND));

        List<CurationExhibit> sampled = reservoirSample(picked.getCurationExhibits(), SUMMARY_CARD_COUNT);

        Set<Long> favoriteExhibitIds = (userId != null) ? favoriteRepository.findActiveExhibitIds(userId) : Set.of();

        return CurationSummaryResult.of(picked, sampled, favoriteExhibitIds);
    }

    @Transactional(readOnly = true)
    public CurationDetailCursorResult getCurationDetail(Long userId, Long curationId, CursorPagination pagination) {
        Curation curation = curationRepository.findById(curationId).orElseThrow(() -> new GeneralException(CurationErrorCode._CURATION_NOT_FOUND));

        if (!curation.isVisibleOn(LocalDate.now())) {
            throw new GeneralException(CurationErrorCode._CURATION_NOT_VISIBLE);
        }

        Set<Long> favoriteExhibitIds = (userId != null) ? favoriteRepository.findActiveExhibitIds(userId) : Set.of();

        int size = pagination.size().intValue();
        Slice<CurationExhibit> slice = (pagination.cursor() == null)
                ? curationRepository.findExhibitsByCurationId(curationId, PageRequest.ofSize(size))
                : curationRepository.findExhibitsByCurationIdAndCursor(curationId, pagination.cursor().intValue(), PageRequest.ofSize(size));

        return CurationDetailCursorResult.of(curation.getTitle(), slice, favoriteExhibitIds);
    }

    private List<CurationExhibit> reservoirSample(List<CurationExhibit> source, int candidates) {
        List<CurationExhibit> reservoir = new ArrayList<>();
        int index = 0;

        for (CurationExhibit candidate : source) {
            if (index < candidates) {
                reservoir.add(candidate);
            } else {
                int j = ThreadLocalRandom.current().nextInt(index + 1);
                if (j < candidates) {
                    reservoir.set(j, candidate);
                }
            }
            index++;
        }
        return reservoir;
    }

}
