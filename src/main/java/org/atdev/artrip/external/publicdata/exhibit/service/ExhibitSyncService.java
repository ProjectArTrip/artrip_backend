package org.atdev.artrip.external.publicdata.exhibit.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.atdev.artrip.domain.exhibitHall.repository.ExhibitHallRepository;
import org.atdev.artrip.external.publicdata.exhibit.client.ExhibitApiClient;
import org.atdev.artrip.external.publicdata.exhibit.dto.response.ExhibitItem;
import org.atdev.artrip.external.publicdata.exhibit.dto.response.ExhibitResponse;
import org.atdev.artrip.external.publicdata.exhibit.mapper.ExhibitMapper;
import org.atdev.artrip.global.apipayload.exception.ExternalApiException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExhibitSyncService {

    private final ExhibitApiClient exhibitApiClient;
    private final ExhibitMapper exhibitMapper;
    private final ExhibitRepository exhibitRepository;
    private final ExhibitHallRepository exhibitHallRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int MAX_PAGES = 100;

    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledSync() {
        log.info("----- 전시 정보 스케줄 동기화 start -----");

        String from = LocalDate.now().format(DATE_FORMATTER);
        String to = LocalDate.now().plusMonths(3).format(DATE_FORMATTER);

        SyncResult result = syncByPeriod(from, to);

        log.info("----- 전시 정보 스케줄 동기화 done -----");
        log.info("result - new: {}, updated: {}, failed: {}",
                result.getInserted(), result.getUpdated(), result.getFailed());
    }

    @Transactional
    public SyncResult syncAll() {
        return executeSync(pageNo -> exhibitApiClient.fetchExhibits(pageNo));
    }

    @Transactional
    public SyncResult syncByPeriod(String from, String to) {
        return executeSync(pageNo -> exhibitApiClient.fetchByPeriod(from, to, pageNo));
    }

    private SyncResult executeSync(Function<Integer, ExhibitResponse> fetcher) {
        SyncResult totalResult = new SyncResult();
        int pageNo = 1;

        try {
            while (pageNo <= MAX_PAGES) {
                ExhibitResponse response = fetcher.apply(pageNo);

                if (!response.isSuccess() || !response.hasData()) {
                    log.info("no date found - {}", pageNo);
                    break;
                }

                SyncResult pageResult = processItems(response.getItems());
                totalResult.merge(pageResult);

                log.debug("페이지 {} 처리 완료 - new: {}, updated: {}, failed: {}",
                        pageNo, pageResult.getInserted(), pageResult.getUpdated(), pageResult.getFailed());

                if (response.isLastPage()) {
                    break;
                }

                pageNo++;
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("동기화 중단됨", e);
        } catch (ExternalApiException e) {
            log.error("API 요청 failed - page: {}, error: {}", pageNo, e.getMessage());
        }

        log.info("동기화 완료 - new: {}, updated: {}, failed: {}",
                totalResult.getInserted(), totalResult.getUpdated(), totalResult.getFailed());

        return totalResult;
    }

    private SyncResult processItems(List<ExhibitItem> items) {
        SyncResult result = new SyncResult();
        Map<String, ExhibitHall> hallCache = new HashMap<>();

        for (ExhibitItem item : items) {
            try {
                processItem(item, hallCache, result);
            } catch (Exception e) {
                log.warn("처리 실패 - title: {}, error: {}", item.getTitle(), e.getMessage());
                result.incrementFailed();
            }
        }
        return result;
    }

    private void processItem(ExhibitItem item, Map<String, ExhibitHall> hallCache, SyncResult result) {
        ExhibitHall exhibitHall = getOrCreateExhibitHall(item, hallCache);

        Exhibit exhibit = exhibitMapper.toExhibit(item);
        if (exhibit == null) {
            result.incrementFailed();
            return;
        }

        exhibit.setExhibitHall(exhibitHall);

        Optional<Exhibit> existing = exhibitRepository
                .findByTitleAndStartDate(exhibit.getTitle(), exhibit.getStartDate());

        if (existing.isPresent()) {
            updateExhibit(existing.get(), exhibit);
            result.incrementUpdated();
        } else {
            exhibitRepository.save(exhibit);
            result.incrementInserted();
        }
    }

    private ExhibitHall getOrCreateExhibitHall(ExhibitItem item, Map<String, ExhibitHall> cache) {
        String placeName = item.getPlace();
        if (placeName == null || placeName.isBlank()) {
            return null;
        }

        if (cache.containsKey(placeName)) {
            ExhibitHall cached = cache.get(placeName);
            updateCoordinatesIfNeeded(cached, item);
            return cached;
        }

        Optional<ExhibitHall> existing = exhibitHallRepository.findByName(placeName);
        if (existing.isPresent()) {
            ExhibitHall hall = existing.get();
            updateCoordinatesIfNeeded(hall, item);
            cache.put(placeName, hall);
            return hall;
        }

        ExhibitHall newHall = exhibitMapper.toExhibitHall(item);
        if (newHall != null) {
            newHall = exhibitHallRepository.save(newHall);
            cache.put(placeName, newHall);
        }

        return newHall;
    }

    private void updateExhibit(Exhibit existing, Exhibit newData) {
        existing.setDescription(newData.getDescription());
        existing.setEndDate(newData.getEndDate());
        existing.setStatus(newData.getStatus());
        existing.setPosterUrl(newData.getPosterUrl());
        existing.setTicketUrl(newData.getTicketUrl());
        existing.setUpdatedAt(LocalDateTime.now());
    }

    private void updateCoordinatesIfNeeded(ExhibitHall hall, ExhibitItem item) {
        if (hall.getLatitude() == null && item.getGpsX() != null) {
            hall.setLatitude(parseCoordinate(item.getGpsX()));
            hall.setLongitude(parseCoordinate(item.getGpsY()));
            exhibitHallRepository.save(hall);
        }
    }

    private BigDecimal parseCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(coordinate.trim());
        } catch (NumberFormatException e) {
            log.warn("좌표 파싱 실패: {}", coordinate);
            return null;
        }
    }

    @Getter
    public static class SyncResult {
        private int inserted = 0;
        private int updated = 0;
        private int failed = 0;

        public void incrementInserted() { inserted++; }
        public void incrementUpdated() { updated++; }
        public void incrementFailed() { failed++; }

        public void merge(SyncResult other) {
            this.inserted += other.inserted;
            this.updated += other.updated;
            this.failed += other.failed;
        }
    }
}
