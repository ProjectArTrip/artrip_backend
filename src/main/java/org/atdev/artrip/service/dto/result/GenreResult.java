package org.atdev.artrip.service.dto.result;

public record GenreResult(String name) {

    public static GenreResult from(String name) {
        return new GenreResult(name);
    }
}
