package org.atdev.artrip.service.strategy.favorite;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.service.dto.command.FavoriteCondition;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomesticStrategy implements FavoriteSortStrategy {

    private final FavoriteRepository favoriteRepository;

    @Override
    public Slice<Favorite> sort(FavoriteCondition condition) {

        SortType type = SortType.fromCode(condition.sortType());
        Sort sort;

        if (type == SortType.ENDING_SOON) {
            sort = Sort.by("exhibit.endDate").ascending().and(Sort.by("createdAt").descending());
        } else {
            sort = Sort.by("createdAt").descending();
        }

        PageRequest pageRequest = PageRequest.of(0, condition.size().intValue(), sort);

        Long cursor = (type == SortType.ENDING_SOON) ? null : condition.cursor();

        return favoriteRepository.findFavorites(
                condition.userId(),
                true,
                null,
                condition.region(),
                cursor,
                Status.FINISHED,
                pageRequest
        );
    }
}
