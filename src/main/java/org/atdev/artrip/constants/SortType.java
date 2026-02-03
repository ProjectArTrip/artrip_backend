package org.atdev.artrip.constants;

import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

public enum SortType {
    POPULAR,
    LATEST,
    ENDING_SOON;

    public static SortType from (String type) {
        if (type == null || type.isBlank()){
            return SortType.LATEST;
        }
        try {
            return SortType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e){
            throw new GeneralException(FavoriteErrorCode._INVALID_SORT_TYPE);
        }
    }
}
