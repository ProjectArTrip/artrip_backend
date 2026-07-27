package org.atdev.artrip.service;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.exhibitSync.ExhibitSync;
import org.atdev.artrip.infra.opendata.CanonicalExhibit;
import org.atdev.artrip.infra.opendata.CanonicalExhibitDetail;
import org.atdev.artrip.infra.opendata.CanonicalPage;
import org.atdev.artrip.infra.opendata.ExhibitSourceConnector;
import org.atdev.artrip.repository.ExhibitHallRepository;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.ExhibitSyncRepository;
import org.atdev.artrip.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExhibitSyncServiceTest {

    private static final String SRC = "KCISA_CULTURE_INFO";

    @Mock
    ExhibitSourceConnector connector;

    @Mock
    ExhibitSyncRepository exhibitSyncRepository;

    @Mock
    ExhibitRepository exhibitRepository;

    @Mock
    ExhibitHallRepository exhibitHallRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    PlatformTransactionManager transactionManager;

    ExhibitSyncService service;
    ExhibitHall hall;

    private User adminUser;

    @BeforeEach
    void setUp() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        service = new ExhibitSyncService(List.of(connector), exhibitSyncRepository,
                exhibitRepository, exhibitHallRepository, userRepository, eventPublisher, transactionTemplate);
        hall = ExhibitHall.of(10L, "국립중앙박물관", "한국", "서울", true);
    }

    private CanonicalExhibit canonical(String ext, String title) {
        return new CanonicalExhibit(ext, title, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), "국립중앙박물관",
                "서울", "http://poster.jpg", new BigDecimal("37.5"), new BigDecimal("126.9"));
    }

    private CanonicalExhibitDetail detail() {
        return new CanonicalExhibitDetail("설명", "http://ticket", "02-1", "서울 용산구", "https://home");
    }

    @Test
    @DisplayName("hash가 동일하면 아무 write도 detail 호출도 없다")
    void skip_whenHashUnchanged() {
        // given
        CanonicalExhibit c = new CanonicalExhibit("1", "전시A", LocalDate.now().minusDays(1), LocalDate.now().plusDays(30),
                "국립중앙박물관", "서울", "http://p.jpg", new BigDecimal("37.5"), new BigDecimal("126.9"));
        Exhibit exhibit = Exhibit.of(100L, "전시A", hall, Status.ONGOING, c.startDate(), c.endDate());
        when(connector.sourceCode()).thenReturn(SRC);
        when(connector.fetchPage(1, 100)).thenReturn(new CanonicalPage(List.of(c), 1));
        when(exhibitSyncRepository.findBySourceAndExternalIdIn(eq(SRC), any()))
                .thenReturn(List.of(ExhibitSync.create(exhibit, SRC, "1", c.contentHash())));

        // when
        service.syncAll();

        // then
        verify(connector, never()).fetchDetail(any());
        verify(exhibitRepository, never()).save(any());
        verify(exhibitSyncRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

}
