package org.atdev.artrip.external.culturalapi.cultureinfo.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.atdev.artrip.domain.exhibitHall.repository.ExhibitHallRepository;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.elastic.service.ExhibitIndexService;
import org.atdev.artrip.external.culturalapi.cultureinfo.client.CultureInfoApiClient;
import org.atdev.artrip.external.culturalapi.cultureinfo.dto.response.CultureInfoDetailItem;
import org.atdev.artrip.external.culturalapi.cultureinfo.dto.response.CultureInfoDetailResponse;
import org.atdev.artrip.external.culturalapi.cultureinfo.dto.response.CultureInfoItem;
import org.atdev.artrip.external.culturalapi.cultureinfo.dto.response.CultureInfoListResponse;
import org.atdev.artrip.external.culturalapi.cultureinfo.mapper.CultureInfoMapper;
import org.atdev.artrip.global.apipayload.exception.ExternalApiException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
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
public class CultureInfoSyncService {

    private final CultureInfoApiClient cultureInfoApiClient;
    private final CultureInfoMapper cultureInfoMapper;
    private final ExhibitRepository exhibitRepository;
    private final ExhibitHallRepository exhibitHallRepository;
    private final KeywordMatchingService  keywordMatchingService;
    private final ExhibitIndexService exhibitIndexService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int MAX_PAGES = 200;
    private static final long DETAIL_API_MS = 500;

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

    public SyncResult syncLatest() {
        String from = LocalDate.now().format(DATE_FORMATTER);
        log.info("오늘[{}] 이후 동기화 ", from);
        return executeSync(pageNo -> cultureInfoApiClient.fetchFromDate(from, pageNo));
    }

    public SyncResult syncAll() {
        return executeSync(pageNo -> cultureInfoApiClient.fetchExhibits(pageNo));
    }

    public SyncResult syncByPeriod(String from, String to) {
        return executeSync(pageNo -> cultureInfoApiClient.fetchByPeriod(from, to, pageNo));
    }

    private SyncResult executeSync(Function<Integer, CultureInfoListResponse> fetcher) {
        SyncResult totalResult = new SyncResult();
        int pageNo = 1;

        try {
            while (pageNo <= MAX_PAGES) {
                CultureInfoListResponse response = fetcher.apply(pageNo);

                if (response.hasData() && !response.getExhibits().isEmpty()) {
                    String firstTitle = response.getExhibits().get(0).getTitle();
                    log.info("Page No :{}, first item : {}", pageNo, firstTitle );
                }

                if (!response.isSuccess() || !response.hasData()) {
                    log.info("no date found - {}", pageNo);
                    break;
                }

                List<CultureInfoItem> exhibitOnly = response.getExhibits();
                if (!exhibitOnly.isEmpty()) {
                    SyncResult pageResult = processPage(exhibitOnly);
                    totalResult.merge(pageResult);

                    log.debug("페이지 {} 처리 완료 - new: {}, updated: {}, failed: {}",
                            pageNo, pageResult.getInserted(), pageResult.getUpdated(), pageResult.getFailed());

                }

                if (response.isLastPage()) {
                    break;
                }

                pageNo++;
                Thread.sleep(300);
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

    @Transactional
    public SyncResult processPage(List<CultureInfoItem> items) {
        SyncResult result = new SyncResult();
        Map<String, ExhibitHall> hallCache = new HashMap<>();

        for (CultureInfoItem item : items) {
            try {
                processItem(item, hallCache, result);
            } catch (Exception e) {
                log.debug("처리 실패 - title: {}, error : {}", item.getTitle(), e.getMessage());
                result.incrementFailed();
            }
        }
        return result;
    }

    private void processItem(CultureInfoItem item, Map<String, ExhibitHall> hallCache, SyncResult result) {
        if (!item.isValid()) {
            result.incrementSkipped();
            return;
        }

        Exhibit exhibit = cultureInfoMapper.toExhibit(item);
        if (exhibit == null) {
            result.incrementSkipped();
            return;
        }

        if (exhibit.getStartDate() == null) {
            result.incrementSkipped();
            return;
        }

        if (exhibit.getEndDate() != null && exhibit.getEndDate().isBefore(LocalDate.now())) {
            result.incrementSkipped();
            return;
        }

        ExhibitHall exhibitHall = getOrCreateExhibitHall(item, hallCache);
        exhibit.setExhibitHall(exhibitHall);

        CultureInfoDetailItem detailItem = fetchDetailIfNeeded(item);

        if (detailItem != null) {
            cultureInfoMapper.mergeDetailToExhibit(exhibit, detailItem);
            if (exhibitHall != null) {
                cultureInfoMapper.mergeDetailToHall(exhibitHall, detailItem);
            }
        }

        Optional<Exhibit> existing = exhibitRepository
                .findByTitleAndStartDate(exhibit.getTitle(), exhibit.getStartDate());

        if (existing.isPresent()) {
            updateExhibit(existing.get(), exhibit);
            matchAndSaveKeywords(existing.get(), item, detailItem);

            exhibitIndexService.indexExhibit(existing.get());
            result.incrementUpdated();
        } else {
            Exhibit saved = exhibitRepository.save(exhibit);
            matchAndSaveKeywords(saved, item, detailItem);
            exhibitRepository.save(saved);

            exhibitIndexService.indexExhibit(saved);
            result.incrementInserted();
        }
    }

    private CultureInfoDetailItem fetchDetailIfNeeded(CultureInfoItem item) {
        if (item.getSeq() == null || item.getSeq().isBlank()) return null;

        try {
            Thread.sleep(DETAIL_API_MS);

            CultureInfoDetailResponse detailResponse = cultureInfoApiClient.fetchDetails(item.getSeq());

            if (detailResponse != null && detailResponse.isSuccess() && detailResponse.hasData()) {
                log.debug("상세 정보 조회 - seq : {} title : {}", item.getSeq(), item.getTitle());
                return detailResponse.getItem();
            } else {
                log.debug("상세 정보 없음 - seq: {}", item.getSeq());
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("상세 조회 중단 - seq : {}", item.getSeq());
            return null;
        } catch (Exception e) {
            log.warn("상세 정보 조회 실패 - seq : {}, error: {}", item.getSeq(), e.getMessage());
            return null;
        }
    }

    private void matchAndSaveKeywords(Exhibit exhibit, CultureInfoItem item, CultureInfoDetailItem detail) {
        String searchText = item.getTitle();
        if (detail != null && detail.getContents1() != null) {
            searchText += " " + detail.getContents1();
        }
        List<Keyword> keywords = keywordMatchingService.matchKeywords(searchText, item.getRealmName());
        exhibit.getKeywords().addAll(keywords);
    }

    private ExhibitHall getOrCreateExhibitHall(CultureInfoItem item, Map<String, ExhibitHall> cache) {
        String placeName = item.getPlace();
        if (placeName == null || placeName.isBlank()) {
            return null;
        }

        if (cache.containsKey(placeName)) {
            ExhibitHall cached = cache.get(placeName);
            updateHallIfNeeded(cached, item);
            return cached;
        }

        Optional<ExhibitHall> existing = exhibitHallRepository.findByName(placeName);
        if (existing.isPresent()) {
            ExhibitHall hall = existing.get();
            updateHallIfNeeded(hall, item);
            cache.put(placeName, hall);
            return hall;
        }

        ExhibitHall newHall = cultureInfoMapper.toExhibitHall(item);
        if (newHall != null) {
            newHall = exhibitHallRepository.save(newHall);
            cache.put(placeName, newHall);
        }

        return newHall;
    }

    private void updateExhibit(Exhibit existing, Exhibit newData) {
        if (newData.getDescription() != null) {
            existing.setDescription(newData.getDescription());
        }
        if (newData.getEndDate() != null) {
            existing.setEndDate(newData.getEndDate());
        }
        if (newData.getStatus() != null) {
            existing.setStatus(newData.getStatus());
        }
        if (newData.getPosterUrl() != null) {
            existing.setPosterUrl(newData.getPosterUrl());
        }
        if (newData.getTicketUrl() != null) {
            existing.setTicketUrl(newData.getTicketUrl());
        }
        existing.setUpdatedAt(LocalDateTime.now());
    }

    private void updateHallIfNeeded(ExhibitHall hall, CultureInfoItem item) {
        boolean updated = false;

        if (hall.getLatitude() == null && item.getGpsY() != null) {
            hall.setLatitude(parseCoordinate(item.getGpsY()));
            hall.setLongitude(parseCoordinate(item.getGpsX()));
        }

        if (hall.getRegion() == null && item.getArea() !=null) {
            hall.setRegion(item.getArea());
            updated = true;
        }

        if (hall.getAddress() == null && item.getPlace() != null) {
            hall.setAddress(item.getFullAddress());
            updated = true;
        }

        if (updated) {
            hall.setUpdatedAt(LocalDateTime.now());
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
        private int skipped = 0;

        public void incrementInserted() { inserted++; }
        public void incrementUpdated() { updated++; }
        public void incrementFailed() { failed++; }
        public void incrementSkipped() { skipped++; }

        public void merge(SyncResult other) {
            this.inserted += other.inserted;
            this.updated += other.updated;
            this.failed += other.failed;
            this.skipped += other.skipped;
        }
    }
}
