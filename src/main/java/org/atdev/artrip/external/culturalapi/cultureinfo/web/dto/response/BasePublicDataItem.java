package org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response;

public interface BasePublicDataItem {

    String getUniqueId();

    default boolean isValid() {
        return getUniqueId() != null && !getUniqueId().isBlank();
    }
}
