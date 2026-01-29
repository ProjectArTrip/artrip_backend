package org.atdev.artrip.global.scheduler;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.repository.RecentExhibitRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecentExhibitCleanupScheduler {

    private final RecentExhibitRepository recentExhibitRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupRecentExhibits() {
        LocalDateTime limit = LocalDateTime.now().minusDays(30);
        recentExhibitRepository.deleteByViewAtBefore(limit);
    }
}