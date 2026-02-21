package org.atdev.artrip.repository;

import org.atdev.artrip.service.dto.condition.FavoriteSearchCondition;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.data.domain.Slice;
import org.atdev.artrip.domain.favorite.Favorite;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FavoriteRepositoryCustom {

    boolean existsActive(Long userId, Long exhibitId);

    Set<Long> findActiveExhibitIds(Long userId);

    Slice<Favorite> findFavorites(Long userId, FavoriteSearchCondition condition, CursorPagination cursorPagination);
}

