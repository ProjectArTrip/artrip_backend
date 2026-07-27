package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Country;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibit.event.ExhibitCreatedEvent;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.exhibitSync.ExhibitSync;
import org.atdev.artrip.domain.exhibitSync.ExhibitSyncedEvent;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.opendata.CanonicalExhibit;
import org.atdev.artrip.infra.opendata.CanonicalExhibitDetail;
import org.atdev.artrip.infra.opendata.CanonicalPage;
import org.atdev.artrip.infra.opendata.ExhibitSourceConnector;
import org.atdev.artrip.repository.ExhibitHallRepository;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.ExhibitSyncRepository;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExhibitSyncService {

    private static final int PAGE_SIZE = 100;
    private static final int MAX_PAGES = 1000;

    private final List<ExhibitSourceConnector> connectors;
    private final ExhibitSyncRepository exhibitSyncRepository;
    private final ExhibitRepository exhibitRepository;
    private final ExhibitHallRepository hallRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Async("exhibitSyncExecutor")
    public void triggerSync(Long adminId) {
        User user = userRepository.findById(adminId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }
        syncAll();
    }

    public int syncAll() {
        List<ExhibitCreatedEvent.ExhibitSummary> newSummaries = new ArrayList<>();
        for (ExhibitSourceConnector connector : connectors) {
            newSummaries.addAll(syncSource(connector));
        }
        return newSummaries.size();

    }

    public void syncAndNotify() {
        int newCount = syncAll();
        if (newCount > 0) {
            transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(new ExhibitSyncedEvent(newCount)));
        }
    }

    private List<ExhibitCreatedEvent.ExhibitSummary> syncSource(ExhibitSourceConnector connector) {
        List<ExhibitCreatedEvent.ExhibitSummary> newSummaries = new ArrayList<>();

        int totalPages = 1;
        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {

            CanonicalPage page = connector.fetchPage(pageNo, PAGE_SIZE);

            if (pageNo == 1) {
                totalPages = Math.min(MAX_PAGES,
                Math.max(1, (int) Math.ceil((double) page.totalCount() / PAGE_SIZE)));
            }
            List<CanonicalExhibit> items = page.items();
            List<ExhibitCreatedEvent.ExhibitSummary> pageNew = transactionTemplate.execute(status -> processPage(connector, items));

            newSummaries.addAll(pageNew);
        }

        return newSummaries;
    }

    private List<ExhibitCreatedEvent.ExhibitSummary> processPage(ExhibitSourceConnector connector, List<CanonicalExhibit> items) {
        if (items.isEmpty()) {
            return List.of();
        }
        String source = connector.sourceCode();

        List<String> externalIds = items.stream().map(CanonicalExhibit::externalId).toList();
        Map<String, ExhibitSync> syncByExternalId = exhibitSyncRepository.findBySourceAndExternalIdIn(source, externalIds).stream()
                .collect(Collectors.toMap(ExhibitSync::getExternalId, sync -> sync));

        Set<String> placeNames = items.stream().map(CanonicalExhibit::placeName).collect(Collectors.toSet());
        Map<String, ExhibitHall> hallByName = new HashMap<>();
        hallRepository.findByNameIn(placeNames).forEach(hall -> hallByName.put(hall.getName(), hall));

        List<ExhibitCreatedEvent.ExhibitSummary> newSummaries = new ArrayList<>();
        for (CanonicalExhibit canonical : items) {
            ExhibitSync existing = syncByExternalId.get(canonical.externalId());
            if (existing != null) {
                updateIfChanged(connector, existing, canonical);
            } else {
                insertNew(connector, canonical, hallByName).ifPresent(newSummaries::add);
            }
        }
        return newSummaries;
    }

    private void updateIfChanged(ExhibitSourceConnector connector, ExhibitSync sync, CanonicalExhibit canonical) {
        String hash = canonical.contentHash();
        if (sync.isSameContent(hash)) {
            return;
        }

        CanonicalExhibitDetail detail = connector.fetchDetail(canonical.externalId());

        Exhibit exhibit = sync.getExhibit();
        String ticketUrl = detail != null ? detail.ticketUrl() : exhibit.getTicketUrl();
        exhibit.updateBasicInfo(
                canonical.title(),
                detail == null ? null : detail.description(),
                canonical.posterUrl(),
                ticketUrl,
                canonical.startDate(),
                canonical.endDate()
        );

        exhibit.changeStatus(resolveStatus(canonical.startDate(), canonical.endDate()));
        sync.updateHash(hash);
    }

    private Optional<ExhibitCreatedEvent.ExhibitSummary> insertNew(ExhibitSourceConnector connector, CanonicalExhibit canonical, Map<String, ExhibitHall> hallByName) {
        ExhibitHall cachedHall = hallByName.get(canonical.placeName());

        if (cachedHall != null && exhibitRepository.existsByHallAndTitleAndStartDate(cachedHall, canonical.title(), canonical.startDate())) {
            return Optional.empty();
        }
        CanonicalExhibitDetail detail = connector.fetchDetail(canonical.externalId());

        ExhibitHall hall = resolveHall(canonical, detail, hallByName);
        Status status = resolveStatus(canonical.startDate(), canonical.endDate());

        Exhibit exhibit = Exhibit.create(
                canonical.title(),
                detail == null ? null : detail.description(),
                canonical.posterUrl(),
                detail == null ? null : detail.ticketUrl(),
                canonical.startDate(),
                canonical.endDate(),
                status,
                hall
        );

        Exhibit saved = exhibitRepository.save(exhibit);
        exhibitSyncRepository.save(ExhibitSync.create(saved, connector.sourceCode(), canonical.externalId(), canonical.contentHash()));
        return Optional.of(ExhibitCreatedEvent.ExhibitSummary.from(saved));
    }

    private ExhibitHall resolveHall(CanonicalExhibit canonical, CanonicalExhibitDetail detail,  Map<String, ExhibitHall> hallByName) {
        return hallByName.computeIfAbsent(canonical.placeName(), name -> hallRepository.save(ExhibitHall.create(
                name,
                Country.KOREA.getLabel(),
                canonical.region(),
                detail == null ? null : detail.placeAddr(),
                null,
                detail == null ? null : detail.phone(),
                canonical.latitude(),
                canonical.longitude(),
                true
        )));
    }

    private Status resolveStatus(LocalDate start, LocalDate end) {
        if (start == null && end == null) return Status.ONGOING;
        LocalDate today = LocalDate.now();
        if (end == null || start == null) return Status.UPCOMING;
        if (end.isBefore(today)) return Status.FINISHED;
        if (start.isAfter(today)) return Status.UPCOMING;
        if (!end.isAfter(today.plusDays(3))) return Status.ENDING_SOON;
        return Status.ONGOING;
    }
}
