package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.FavoriteCondition;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public FavoriteResult getFavorites(FavoriteCondition condition) {
        if (!userRepository.existsById(condition.userId())) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        Slice<Favorite> slice;
        PageRequest pageRequest = PageRequest.ofSize(condition.size().intValue());

        if(Boolean.TRUE.equals(condition.isDomestic())) {
            slice = switch (condition.sortType()) {
                case ENDING_SOON -> favoriteRepository.findDomesticByRegion(
                        condition.userId(),
                        condition.region(),
                        condition.isDomestic(),
                        condition.cursor(),
                        Status.FINISHED,
                        pageRequest
                );
                case LATEST -> favoriteRepository.findDomesticByRegionEndingSoon(
                        condition.userId(),
                        condition.region(),
                        condition.isDomestic(),
                        condition.cursor(),
                        Status.FINISHED,
                        pageRequest
                );
                case POPULAR -> throw new GeneralException(FavoriteErrorCode._INVALID_SORT_TYPE);
            };
    } else if (Boolean.FALSE.equals(condition.isDomestic())) {
            slice = switch (condition.sortType()) {
                case ENDING_SOON -> favoriteRepository.findOverseasByCountryEndingSoon(
                        condition.userId(),
                        condition.country(),
                        condition.region(),
                        condition.isDomestic(),
                        condition.cursor(),
                        Status.FINISHED,
                        pageRequest
                );
                case LATEST -> favoriteRepository.findOverseasByCountry(
                        condition.userId(),
                        condition.country(),
                        condition.region(),
                        condition.isDomestic(),
                        condition.cursor(),
                        Status.FINISHED,
                        pageRequest
                );
                case POPULAR -> throw new GeneralException(FavoriteErrorCode._INVALID_SORT_TYPE);
            };
        } else {
            slice = switch (condition.sortType()) {
                case ENDING_SOON -> favoriteRepository.findAllActiveByEndDate(
                        condition.userId(),
                        condition.cursor(),
                        Status.FINISHED,
                        pageRequest
                );
                case LATEST -> favoriteRepository.findAllActive(
                        condition.userId(),
                        condition.cursor(),
                        Status.FINISHED,
                        pageRequest
                );
                case POPULAR -> throw new GeneralException(FavoriteErrorCode._INVALID_SORT_TYPE);
            };
        }
        return FavoriteResult.of(slice);
    }
}
