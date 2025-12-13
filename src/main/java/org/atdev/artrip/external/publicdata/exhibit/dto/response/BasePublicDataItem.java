package org.atdev.artrip.external.publicdata.exhibit.dto.response;

public interface BasePublicDataItem {

    String getUniqueId();

    default boolean isValid() {
        return getUniqueId() != null && !getUniqueId().isBlank();
    }
}
