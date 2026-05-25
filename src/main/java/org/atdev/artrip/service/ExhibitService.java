package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitMarkerMeta;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.global.apipayload.code.error.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.dto.ExhibitMarkerDto;
import org.atdev.artrip.service.dto.result.ExhibitDetailResult;
import org.atdev.artrip.service.dto.command.ExhibitDetailCommand;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.atdev.artrip.service.dto.result.ExhibitMarkerListResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserHistoryService userHistoryService;

    @Transactional(readOnly = true)
    public ExhibitDetailResult getExhibitDetail(ExhibitDetailCommand command) {

        Exhibit exhibit = exhibitRepository.findByIdWithHall(command.exhibitId())
                .orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));

        boolean isFavorite = false;

        if (command.userId() != null) {
            isFavorite = favoriteRepository.existsActive(command.userId(), command.exhibitId());
            userHistoryService.addRecentView(command.userId(), exhibit);
        }

        return ExhibitDetailResult.of(exhibit, isFavorite);
    }

    @Transactional(readOnly = true)
    public ExhibitFilterResult getClusterExhibit(List<Long> ids, CursorPagination pagination, Long userId){

        if (ids == null || ids.isEmpty()) {
            return ExhibitFilterResult.of(null, Set.of(), 0L);
        }

        Long cursorId = pagination.cursor();
        int size = pagination.size().intValue();

        Slice<Exhibit> slice;
        if (cursorId == null) {
            slice = exhibitRepository.findByIdInOrderByIdDesc(ids, PageRequest.ofSize(size));
        } else {
            slice = exhibitRepository.findByIdInAndIdLessThanOrderByIdDesc(ids, cursorId, PageRequest.ofSize(size));
        }

        Set<Long> favoriteIds = (userId != null) ? getFavoriteIds(userId) : Set.of();

        return ExhibitFilterResult.of(slice,favoriteIds, 0L);
    }

    private Set<Long> getFavoriteIds(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        return favoriteRepository.findActiveExhibitIds(userId);
    }

    @Transactional(readOnly = true)
    public ExhibitMarkerListResult getMarkers() {

        List<ExhibitMarkerDto> dtos = exhibitRepository.findMarkersByStatus(MARKER_STATUSES);

        return ExhibitMarkerListResult.from(dtos);
    }

    @Transactional(readOnly = true)
    public String getMarkerEtag() {

        ExhibitMarkerMeta meta = exhibitRepository.findMarkerMeta(MARKER_STATUSES);

        long count = 0L;
        LocalDateTime lastUpdated = null;

        if (meta != null) {
            count = meta.getCount();
            lastUpdated = meta.getLastUpdated();
        }

        return "\"" + count + ":" + (lastUpdated != null ? lastUpdated : "empty") + "\"";
    }

    private static final List<Status> MARKER_STATUSES =
            List.of(Status.ONGOING, Status.ENDING_SOON);
}
