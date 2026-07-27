package org.atdev.artrip.domain.exhibitSync;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExhibitSyncedEventTest {

    @Test
    @DisplayName("newCount 값을 그대로 보관하고 접근할 수 있다")
    void newCount_isAccessibleViaAccessor() {
        ExhibitSyncedEvent event = new ExhibitSyncedEvent(5);

        assertThat(event.newCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("동일한 newCount를 가진 이벤트는 동등하다 (record equals)")
    void events_withSameCount_areEqual() {
        ExhibitSyncedEvent a = new ExhibitSyncedEvent(3);
        ExhibitSyncedEvent b = new ExhibitSyncedEvent(3);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}