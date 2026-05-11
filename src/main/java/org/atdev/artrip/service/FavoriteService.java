package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.global.apipayload.code.error.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.code.error.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.condition.FavoriteSearchCondition;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional(readOnly = true)
    public FavoriteResult getFavorites(Long userId, FavoriteSearchCondition condition, CursorPagination cursorPagination) {

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        SortType sortType = condition.sortType();

        if (sortType == null || sortType == SortType.NONE) {
            sortType = SortType.LATEST;
        }

        if (sortType == SortType.POPULAR) {
            throw new GeneralException(FavoriteErrorCode._UNSUPPORTED_SORT_TYPE);
        }

        FavoriteSearchCondition normalized = new FavoriteSearchCondition(sortType, condition.region(), condition.country());

        Slice<Favorite> slice = favoriteRepository.findFavorites(userId, normalized, cursorPagination);

        long totalCount = favoriteRepository.countFavorites(userId, normalized);

        return FavoriteResult.of(slice, totalCount);
    }

    @Transactional
    public void create(Long userId, Long exhibitId) {

        User user = userRepository.findByUserId(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        Exhibit exhibit = exhibitRepository.findById(exhibitId).orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));

        favoriteRepository.findFavorite(userId, exhibitId).ifPresentOrElse(favorite -> {
            if (favorite.isStatus()) {
                throw new GeneralException(FavoriteErrorCode._FAVORITE_ALREADY_EXISTS);
            }
            favorite.activate();
        }, () -> favoriteRepository.save(Favorite.create(user, exhibit)));
    }

    @Transactional
    public void delete(Long userId, Long exhibitId) {
        userRepository.findByUserId(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
        Favorite favorite = favoriteRepository.findFavorite(userId, exhibitId).orElseThrow(() -> new GeneralException(FavoriteErrorCode._FAVORITE_NOT_FOUND));

        if (!favorite.isStatus()){
            throw new GeneralException(FavoriteErrorCode._FAVORITE_NOT_FOUND);
        }

        favorite.deactivate();
    }
}
