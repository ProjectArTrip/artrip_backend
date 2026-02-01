package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.GenreListResult;

public record GenreListResponse(
        GenreListResult result
) {
    public static GenreListResponse from(GenreListResult results) {
        return new GenreListResponse(results);
    }

}