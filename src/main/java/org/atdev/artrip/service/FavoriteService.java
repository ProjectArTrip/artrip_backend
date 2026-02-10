package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.FavoriteCondition;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.atdev.artrip.service.strategy.favorite.FavoriteSortStrategy;
import org.atdev.artrip.service.strategy.favorite.FavoriteStrategyFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final FavoriteStrategyFactory favoriteStrategyFactory;

    public FavoriteResult getFavorites(FavoriteCondition condition) {
        if (!userRepository.existsById(condition.userId())) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        FavoriteSortStrategy strategy = favoriteStrategyFactory.getStrategy(condition.isDomestic());

        Slice<Favorite> slice = strategy.sort(condition);

        return FavoriteResult.of(slice);

    }
}
