package org.atdev.artrip.service.dto.result;

import java.util.List;

public record GenreListResult(
        List<GenreResult> genres
) {
    public static GenreListResult from(List<String> genreNames){

        List<GenreResult> results = genreNames.stream()
                .map(GenreResult::from)
                .toList();
        return new GenreListResult(results);
    }
}
