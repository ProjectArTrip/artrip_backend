package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.FavoriteRepositoryCustom;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.condition.FavoriteSearchCondition;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final FavoriteRepositoryCustom favoriteRepositoryCustom;

    public FavoriteResult getFavorites(Long userId, FavoriteSearchCondition condition, CursorPagination cursorPagination) {

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        SortType type = SortType.fromCode(condition.sortType().getCode());

        if (type == SortType.POPULAR) {
            throw new GeneralException(FavoriteErrorCode._UNSUPPORTED_SORT_TYPE);
        }

        Slice<Favorite> slice = favoriteRepositoryCustom.findFavorites(userId, condition, cursorPagination);

        return FavoriteResult.from(slice);
    }

}
