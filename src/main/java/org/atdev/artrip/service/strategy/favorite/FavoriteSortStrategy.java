package org.atdev.artrip.service.strategy.favorite;

import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.service.dto.command.FavoriteCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FavoriteSortStrategy {
    Slice<Favorite> sort (FavoriteCondition condition);
}
