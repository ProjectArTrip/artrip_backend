package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.curation.CurationExhibit;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.global.apipayload.code.status.CurationErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.CurationRepository;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.service.dto.result.CurationDetailCursorResult;
import org.atdev.artrip.service.dto.result.CurationSummaryListResult;
import org.atdev.artrip.service.dto.result.CurationSummaryResult;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.atdev.artrip.utils.CursorPagination;
import org.atdev.artrip.utils.DateTimeUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public CurationSummaryListResult getSummaryCuration(Long userId, String country) {
        LocalDate today = LocalDate.now();

        boolean allCountry = country == null || country.isBlank() || "전체".equals(country);

        List<Curation> curations = allCountry
                ? curationRepository.findVisibleCurations(today)
                : curationRepository.findVisibleCurationsByCountry(country, today);

        Set<Long> favoriteExhibitIds = (userId != null) ? favoriteRepository.findActiveExhibitIds(userId) : Set.of();

        List<CurationSummaryResult> summaries = curations.stream()
                .map(curation -> {
                    List<CurationExhibit> candidates = curation.getCurationExhibits();

                    if (candidates.isEmpty()) {
                        return null;
                    }

                    List<CurationSummaryResult.ExhibitItem> exhibitResults = reservoirSample(candidates, SUMMARY_CARD_COUNT).stream()
                            .map(ce -> {
                                Exhibit exhibit = ce.getExhibit();
                                ExhibitHall hall = exhibit.getExhibitHall();
                                String location = hall.getIsDomestic() ? hall.getRegion() : hall.getCountry();

                                return new CurationSummaryResult.ExhibitItem(
                                        exhibit.getExhibitId(),
                                        exhibit.getPosterUrl(),
                                        exhibit.getTitle(),
                                        hall.getName(),
                                        location,
                                        DateTimeUtils.convertDate(exhibit.getStartDate(), exhibit.getEndDate()),
                                        favoriteExhibitIds.contains(exhibit.getExhibitId())
                                );
                            })
                            .toList();

                    return CurationSummaryResult.of(
                            curation.getCurationId(),
                            curation.getTitle(),
                            curation.getSubtitle(),
                            exhibitResults
                    );
                })
                .filter(result -> result != null).toList();

        return CurationSummaryListResult.from(summaries);

    }

    @Transactional(readOnly = true)
    public CurationDetailCursorResult getCurationDetail(Long userId, Long curationId, CursorPagination pagination) {
        Curation curation = curationRepository.findById(curationId).orElseThrow(() -> new GeneralException(CurationErrorCode._CURATION_NOT_FOUND));

        if (!curation.isActive()) {
            throw new GeneralException(CurationErrorCode._CURATION_NOT_VISIBLE);
        }

        Set<Long> favoriteExhibitIds = (userId != null) ? favoriteRepository.findActiveExhibitIds(userId) : Set.of();

        int size = pagination.size().intValue();

        Slice<CurationExhibit> slice = (pagination.cursor() == null)
                ? curationRepository.findExhibitsByCurationId(curationId, PageRequest.ofSize(size))
                : curationRepository.findExhibitsByCurationIdAndCursor(curationId, pagination.cursor().intValue(), PageRequest.ofSize(size));

        List<ExhibitFilterResult.ExhibitItem> items = slice.getContent().stream()
                .map(ce -> {
                    Exhibit exhibit = ce.getExhibit();

                    return new ExhibitFilterResult.ExhibitItem(
                            exhibit.getExhibitId(),
                            exhibit.getTitle(),
                            exhibit.getPosterUrl(),
                            exhibit.getStatus(),
                            DateTimeUtils.convertDate(exhibit.getStartDate(), exhibit.getEndDate()),
                            favoriteExhibitIds.contains(exhibit.getExhibitId()),
                            exhibit.getExhibitHall().getName(),
                            exhibit.getExhibitHall().getCountry(),
                            exhibit.getExhibitHall().getRegion()
                    );
                }).toList();

        Long nextCursor = null;
        if (slice.hasNext() && !slice.getContent().isEmpty()) {
            nextCursor = (long) slice.getContent().get(slice.getContent().size() - 1).getDisplayOrder();
        }

        return CurationDetailCursorResult.of(curation.getTitle(), items, slice.hasNext(), nextCursor);

    }


    private List<CurationExhibit> reservoirSample(List<CurationExhibit> n, int k) {
        List<CurationExhibit> reservoir = new ArrayList<>();
        int index = 0;

        for (CurationExhibit candidate : n) {
            if (index < k) {
                reservoir.add(candidate);
            } else {
                int j = ThreadLocalRandom.current().nextInt(index + 1);
                if (j < k) {
                    reservoir.set(j, candidate);
                }
            }
            index++;
        }
        return reservoir;
    }

}
