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
    private static String normalize(String value) {
        return (value == null || "전체".equals(value)) ? null : value;
    }
    public ExhibitRandomCommand {
        region = normalize(region);
        country = normalize(country);
    }


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

