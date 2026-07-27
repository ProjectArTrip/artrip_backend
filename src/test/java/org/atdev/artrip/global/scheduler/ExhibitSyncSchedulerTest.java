package org.atdev.artrip.global.scheduler;

import org.atdev.artrip.service.ExhibitSyncService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ExhibitSyncSchedulerTest {

    @Mock
    ExhibitSyncService exhibitSyncService;

    @Test
    @DisplayName("scheduledSync는 ExhibitSyncService.syncAndNotify를 위임 호출한다")
    void scheduledSync_delegatesToSyncAndNotify() {
        ExhibitSyncScheduler scheduler = new ExhibitSyncScheduler(exhibitSyncService);

        scheduler.scheduledSync();

        verify(exhibitSyncService).syncAndNotify();
        verifyNoMoreInteractions(exhibitSyncService);
    }
}