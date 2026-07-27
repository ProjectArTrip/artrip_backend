package org.atdev.artrip.infra.opendata;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;

public record CanonicalExhibit(
        String externalId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String placeName,
        String region,
        String posterUrl,
        BigDecimal latitude,
        BigDecimal longitude
) {

    public String contentHash() {
        String raw = String.join("|",
                title,
                String.valueOf(startDate),
                String.valueOf(endDate),
                placeName,
                String.valueOf(posterUrl));

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
