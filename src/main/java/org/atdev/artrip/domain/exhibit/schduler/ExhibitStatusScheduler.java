package org.atdev.artrip.domain.exhibit.schduler;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class ExhibitStatusScheduler {

    private final ExhibitRepository exhibitRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Transactional
    public void updateExhibitStatus() {
        exhibitRepository.updateEndingSoonStatus();
        exhibitRepository.updateFinishedStatus();

    }
}
