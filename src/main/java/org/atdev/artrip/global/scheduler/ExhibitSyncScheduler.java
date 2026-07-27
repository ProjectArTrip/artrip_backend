package org.atdev.artrip.global.scheduler;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.service.ExhibitSyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitSyncScheduler {
    private final ExhibitSyncService exhibitSyncService;

    @Async("exhibitSyncExecutor")
    @Scheduled(cron = "${opendata.sync.cron}", zone = "${opendata.sync.zone}")
    public void scheduledSync() {
        exhibitSyncService.syncAndNotify();
    }
}
