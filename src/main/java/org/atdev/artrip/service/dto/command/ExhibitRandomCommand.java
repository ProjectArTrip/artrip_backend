package org.atdev.artrip.service.dto.command;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder(toBuilder = true)
public record ExhibitRandomCommand(
        boolean isDomestic,
        String region,
        String country,
        LocalDate date,
        String singleGenre,
        Long userId,

        Set<String> genres,
        Set<String> styles,

        Integer width,
        Integer height,
        String format,

        int limit
) {

    public ExhibitRandomCommand withKeywords(Set<String> genres, Set<String> styles) {
        return this.toBuilder()
                .genres(genres)
                .styles(styles)
                .limit(3)
                .build();
    }

    public ExhibitRandomCommand withLimit(int newLimit) {
        return this.toBuilder()
                .limit(newLimit)
                .build();
    }

    public ExhibitRandomCommand withGenre() {
        Set<String> genreSet = (this.singleGenre != null) ? Set.of(this.singleGenre) : this.genres;

        return this.toBuilder()
                .genres(genreSet)
                .limit(3)
                .build();
    }
}

