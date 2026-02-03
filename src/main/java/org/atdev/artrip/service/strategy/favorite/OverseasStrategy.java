package org.atdev.artrip.service.strategy.favorite;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.service.dto.command.FavoriteCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OverseasStrategy implements FavoriteSortStrategy{

    private final FavoriteRepository favoriteRepository;

    @Override
    public Slice<Favorite> sortLatest(FavoriteCondition condition, Pageable pageable) {
        return favoriteRepository.findOverseasByCountry(
                condition.userId(),
                condition.country(),
                condition.region(),
                condition.isDomestic(),
                condition.cursor(),
                Status.FINISHED,
                pageable
        );
    }

    @Override
    public Slice<Favorite> sortEndingSoon(FavoriteCondition condition, Pageable pageable) {
        return favoriteRepository.findOverseasByCountryEndingSoon(
                condition.userId(),
                condition.country(),
                condition.region(),
                condition.isDomestic(),
                condition.cursor(),
                Status.FINISHED,
                pageable
        );
    }

}
