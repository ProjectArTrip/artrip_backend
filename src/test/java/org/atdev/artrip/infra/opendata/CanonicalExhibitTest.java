package org.atdev.artrip.infra.opendata;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CanonicalExhibitTest {

    private CanonicalExhibit exhibit(String title, String poster) {
        return new CanonicalExhibit("1", title, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                "place", "region", poster, BigDecimal.ONE, BigDecimal.TEN);
    }

    @Test
    @DisplayName("동일한 내용이면 동일한 hash를 생성한다")
    void contentHash_isDeterministic() {
        CanonicalExhibit a = exhibit("title", "poster");
        CanonicalExhibit b = exhibit("title", "poster");

        assertThat(a.contentHash()).isEqualTo(b.contentHash());
    }

    @Test
    @DisplayName("title이 다르면 hash도 달라진다")
    void contentHash_changesWhenTitleChanges() {
        CanonicalExhibit a = exhibit("title1", "poster");
        CanonicalExhibit b = exhibit("title2", "poster");

        assertThat(a.contentHash()).isNotEqualTo(b.contentHash());
    }

    @Test
    @DisplayName("posterUrl이 다르면 hash도 달라진다")
    void contentHash_changesWhenPosterUrlChanges() {
        CanonicalExhibit a = exhibit("title", "poster1.jpg");
        CanonicalExhibit b = exhibit("title", "poster2.jpg");

        assertThat(a.contentHash()).isNotEqualTo(b.contentHash());
    }

    @Test
    @DisplayName("hash는 64자리 소문자 16진수 문자열(SHA-256)이다")
    void contentHash_isHex64Characters() {
        CanonicalExhibit a = exhibit("title", "poster");

        assertThat(a.contentHash()).hasSize(64).matches("[0-9a-f]+");
    }

    @Test
    @DisplayName("externalId와 region은 hash 계산에 영향을 주지 않는다")
    void contentHash_ignoresExternalIdAndRegion() {
        CanonicalExhibit a = new CanonicalExhibit("1", "title", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                "place", "region-a", "poster", BigDecimal.ONE, BigDecimal.TEN);
        CanonicalExhibit b = new CanonicalExhibit("2", "title", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                "place", "region-b", "poster", BigDecimal.ONE, BigDecimal.TEN);

        assertThat(a.contentHash()).isEqualTo(b.contentHash());
    }
}