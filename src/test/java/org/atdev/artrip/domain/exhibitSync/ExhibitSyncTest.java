package org.atdev.artrip.domain.exhibitSync;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ExhibitSyncTest {

    private Exhibit exhibit;

    @BeforeEach
    void setUp() {
        ExhibitHall hall = ExhibitHall.of(1L, "국립중앙박물관", "한국", "서울", true);
        exhibit = Exhibit.of(1L, "title", hall, Status.ONGOING, LocalDate.now(), LocalDate.now().plusDays(10));
    }

    @Test
    @DisplayName("create()는 전달받은 값으로 초기 상태를 구성하고 syncedAt을 현재 시각으로 설정한다")
    void create_setsInitialState() {
        LocalDateTime before = LocalDateTime.now();

        ExhibitSync sync = ExhibitSync.create(exhibit, "SRC", "ext-1", "hash1");

        LocalDateTime after = LocalDateTime.now();
        assertThat(sync.getExhibit()).isEqualTo(exhibit);
        assertThat(sync.getSource()).isEqualTo("SRC");
        assertThat(sync.getExternalId()).isEqualTo("ext-1");
        assertThat(sync.getContentHash()).isEqualTo("hash1");
        assertThat(sync.getSyncedAt()).isBetween(before, after);
    }

    @Test
    @DisplayName("isSameContent는 동일한 hash일 때 true, 다르면 false를 반환한다")
    void isSameContent_comparesHash() {
        ExhibitSync sync = ExhibitSync.create(exhibit, "SRC", "ext-1", "hash1");

        assertThat(sync.isSameContent("hash1")).isTrue();
        assertThat(sync.isSameContent("hash2")).isFalse();
    }

    @Test
    @DisplayName("updateHash는 contentHash와 syncedAt을 모두 갱신한다")
    void updateHash_updatesHashAndSyncedAt() throws InterruptedException {
        ExhibitSync sync = ExhibitSync.create(exhibit, "SRC", "ext-1", "hash1");
        LocalDateTime originalSyncedAt = sync.getSyncedAt();

        Thread.sleep(5);
        sync.updateHash("hash2");

        assertThat(sync.getContentHash()).isEqualTo("hash2");
        assertThat(sync.getSyncedAt()).isAfterOrEqualTo(originalSyncedAt);
        assertThat(sync.isSameContent("hash2")).isTrue();
        assertThat(sync.isSameContent("hash1")).isFalse();
    }
}