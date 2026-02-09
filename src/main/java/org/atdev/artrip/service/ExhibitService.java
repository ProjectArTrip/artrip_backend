package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.dto.result.ExhibitDetailResult;
import org.atdev.artrip.service.dto.command.ExhibitDetailCommand;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final FavoriteExhibitRepository favoriteExhibitRepository;
    private final UserHistoryService userHistoryService;


    @Transactional(readOnly = true)
    public ExhibitDetailResult getExhibitDetail(ExhibitDetailCommand command) {

        Exhibit exhibit = exhibitRepository.findByIdWithHall(command.exhibitId())
                .orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));

        boolean isFavorite = false;

        if (command.userId() != null) {
            isFavorite = favoriteExhibitRepository.existsActive(command.userId(), command.exhibitId());
            userHistoryService.addRecentView(command.userId(), exhibit);
        }

        return ExhibitDetailResult.of(exhibit, isFavorite);
    }

    @Transactional(readOnly = true)
    public ExhibitFilterResult getClusterExhibit(List<Long> ids, LocalDate cursorDate, Long cursor, int size, Long userId){

        if (ids == null || ids.isEmpty()) {
            return ExhibitFilterResult.of(null,null);
        }

        Slice<Exhibit> slice;
        if (cursor == null) {
            slice = exhibitRepository.findByIdInOrderByIdDesc(ids, PageRequest.ofSize(size));
        } else {
            slice = exhibitRepository.findByIdInAndIdLessThanOrderByIdDesc(ids, cursorDate,cursor, PageRequest.ofSize(size));
        }

        Set<Long> favoriteIds = (userId != null) ? getFavoriteIds(userId) : Set.of();

        return ExhibitFilterResult.of(slice,favoriteIds);
    }

    private Set<Long> getFavoriteIds(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        return favoriteExhibitRepository.findActiveExhibitIds(userId);
    }

}
