package org.atdev.artrip.external.culturalapi.cultureinfo.dto.response;

import java.util.List;

public interface PublicDataResponse<T> {

    boolean isSuccess();

    String getErrorMessage();

    int getTotalPages();

    int getCurrentPage();

    int getTotalCount();

    List<T> getItems();

    default boolean hasData() {
        return !getItems().isEmpty();
    }

    default boolean isLastPage() {
        return getCurrentPage() >= getTotalPages();
    }
}
