package org.atdev.artrip.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledIndexService {

    private final ExhibitIndexService indexService;
    private final ExhibitRepository exhibitRepository;

    @Scheduled(cron = "0 0 2 * * * ")
    public void reindexAllExhibits() {
    log.info("Reindexing all exhibits");

    try {
        int count = indexService.indexAllExhibits();

    } catch (Exception e) {
        log.error("Error during reindexing: {}", e.getMessage(), e);
        throw new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND);
    }
    }

    @Scheduled(fixedDelay = 600000) // 10 minutes
    public void reindexRecentlyExhibits() {

        log.info("Reindexing recently exhibits");

        try {
            // Todo: 최근 1시간 이내 수정된 데이터만 조회해서 재색인

//            List<Exhibit> recentExhibits = exhibitRepository.findByUpdatedAtAfter(
//                    LocalDateTime.now().minusHours(1));
//            recentExhibits.forEach(indexService::indexExhibit);
        } catch (Exception e) {
            log.error("Error during reindexing: {}", e.getMessage(), e);
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND);
        }
    }
}
