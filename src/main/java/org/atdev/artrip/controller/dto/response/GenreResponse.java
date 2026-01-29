package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.GenreResult;

import java.util.List;

public record GenreResponse(String name) {

    public static GenreResponse from(GenreResult result) {
        return new GenreResponse(result.name());
    }

    public static List<GenreResponse> from(List<GenreResult> results) {
        return results.stream()
                .map(GenreResponse::from)
                .toList();
    }
}