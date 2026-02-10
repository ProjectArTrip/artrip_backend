package org.atdev.artrip.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.apipayload.code.status.FavoriteErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

@Getter
@RequiredArgsConstructor
public enum SortType {
    NONE("", "없음"),
    POPULAR("POPULAR", "인기순"),
    LATEST("LATEST", "최신순"),
    ENDING_SOON("ENDING_SOON", "마감순");

    private final String code;
    private final String description;

    public static SortType fromCode(String code) {
        for (SortType type : SortType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new GeneralException(FavoriteErrorCode._UNSUPPORTED_SORT_TYPE);
    }
}
