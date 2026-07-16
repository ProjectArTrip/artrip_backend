package org.atdev.artrip.service.dto.result;

public record AdminCurationCreateResult(
        Long curationId
) {

    public static AdminCurationCreateResult from(Long curationId) {
        return new AdminCurationCreateResult(curationId);
    }
}
