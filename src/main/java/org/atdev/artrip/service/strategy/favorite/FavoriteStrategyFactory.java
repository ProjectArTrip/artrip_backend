package org.atdev.artrip.service.strategy.favorite;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoriteStrategyFactory {

    private final DomesticStrategy domesticStrategy;
    private final OverseasStrategy overseasStrategy;
    private final EntireStrategy entireStrategy;

    public FavoriteSortStrategy getStrategy(Boolean isDomestic) {
        if (Boolean.TRUE.equals(isDomestic)) {
            return domesticStrategy;
        } else if (Boolean.FALSE.equals(isDomestic)) {
            return overseasStrategy;
        }
        return entireStrategy;
    }

}
