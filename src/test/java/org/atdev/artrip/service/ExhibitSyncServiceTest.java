package org.atdev.artrip.service;

import org.atdev.artrip.constants.Role;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.exhibitSync.ExhibitSync;
import org.atdev.artrip.domain.exhibitSync.ExhibitSyncedEvent;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    @DisplayName("신규 전시이고 동일 이름의 전시관이 없으면 전시관을 새로 생성하고 전시/동기화 정보를 저장한다")
    void syncAll_insertsNewExhibit_whenHallDoesNotExist() {
        // given
        CanonicalExhibit c = canonical("100", "새 전시");
        when(connector.sourceCode()).thenReturn(SRC);
        when(connector.fetchPage(1, 100)).thenReturn(new CanonicalPage(List.of(c), 1));
        when(exhibitSyncRepository.findBySourceAndExternalIdIn(eq(SRC), any())).thenReturn(List.of());
        when(exhibitHallRepository.findByNameIn(any())).thenReturn(List.of());
        when(connector.fetchDetail("100")).thenReturn(detail());
        ExhibitHall newHall = ExhibitHall.of(20L, "국립중앙박물관", "한국", "서울", true);
        when(exhibitHallRepository.save(any(ExhibitHall.class))).thenReturn(newHall);
        Exhibit savedExhibit = Exhibit.of(200L, "새 전시", newHall, Status.ONGOING, c.startDate(), c.endDate());
        when(exhibitRepository.save(any(Exhibit.class))).thenReturn(savedExhibit);

        // when
        int result = service.syncAll();

        // then
        assertThat(result).isEqualTo(1);
        verify(exhibitHallRepository).save(any(ExhibitHall.class));
        verify(exhibitRepository).save(any(Exhibit.class));
        verify(exhibitSyncRepository).save(any(ExhibitSync.class));
    }

    @Test
    @DisplayName("동일한 hall/title/startDate 조합이 이미 존재하면 새로 등록하지 않는다")
    void syncAll_skipsInsert_whenDuplicateExistsInSameHall() {
        // given
        CanonicalExhibit c = canonical("101", "중복 전시");
        when(connector.sourceCode()).thenReturn(SRC);
        when(connector.fetchPage(1, 100)).thenReturn(new CanonicalPage(List.of(c), 1));
        when(exhibitSyncRepository.findBySourceAndExternalIdIn(eq(SRC), any())).thenReturn(List.of());
        when(exhibitHallRepository.findByNameIn(any())).thenReturn(List.of(hall));
        when(exhibitRepository.existsByHallAndTitleAndStartDate(hall, c.title(), c.startDate())).thenReturn(true);

        // when
        int result = service.syncAll();

        // then
        assertThat(result).isEqualTo(0);
        verify(connector, never()).fetchDetail(any());
        verify(exhibitRepository, never()).save(any());
        verify(exhibitSyncRepository, never()).save(any());
    }

    @Test
    @DisplayName("hash가 변경되면 상세 정보를 조회해 전시 정보와 상태를 갱신하고 동기화 hash를 업데이트한다")
    void syncAll_updatesExhibit_whenHashChanged() {
        // given
        CanonicalExhibit updated = new CanonicalExhibit("1", "전시A-수정", LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30), "국립중앙박물관", "서울", "http://new-poster.jpg",
                new BigDecimal("37.5"), new BigDecimal("126.9"));
        Exhibit exhibit = Exhibit.of(100L, "전시A", hall, Status.ONGOING, updated.startDate(), updated.endDate());
        ExhibitSync sync = ExhibitSync.create(exhibit, SRC, "1", "old-hash");

        when(connector.sourceCode()).thenReturn(SRC);
        when(connector.fetchPage(1, 100)).thenReturn(new CanonicalPage(List.of(updated), 1));
        when(exhibitSyncRepository.findBySourceAndExternalIdIn(eq(SRC), any())).thenReturn(List.of(sync));
        when(connector.fetchDetail("1")).thenReturn(detail());

        // when
        int result = service.syncAll();

        // then
        assertThat(result).isEqualTo(0);
        assertThat(exhibit.getTitle()).isEqualTo("전시A-수정");
        assertThat(exhibit.getPosterUrl()).isEqualTo("http://new-poster.jpg");
        assertThat(sync.getContentHash()).isEqualTo(updated.contentHash());
        verify(exhibitRepository, never()).save(any());
    }

    @Test
    @DisplayName("totalCount에 따라 필요한 페이지 수만큼 반복 조회한다")
    void syncAll_fetchesAllPages_basedOnTotalCount() {
        // given
        when(connector.sourceCode()).thenReturn(SRC);
        when(connector.fetchPage(1, 100)).thenReturn(new CanonicalPage(List.of(), 250));
        when(connector.fetchPage(2, 100)).thenReturn(new CanonicalPage(List.of(), 250));
        when(connector.fetchPage(3, 100)).thenReturn(new CanonicalPage(List.of(), 250));

        // when
        service.syncAll();

        // then
        verify(connector).fetchPage(1, 100);
        verify(connector).fetchPage(2, 100);
        verify(connector).fetchPage(3, 100);
        verify(connector, never()).fetchPage(4, 100);
    }

    @Test
    @DisplayName("관리자 권한 사용자가 트리거하면 동기화가 수행된다")
    void triggerSync_admin_runsSyncAll() {
        // given
        User admin = User.builder().userId(1L).name("admin").role(Role.ADMIN).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(connector.sourceCode()).thenReturn(SRC);
        when(connector.fetchPage(1, 100)).thenReturn(new CanonicalPage(List.of(), 0));

        // when
        service.triggerSync(1L);

        // then
        verify(connector).fetchPage(1, 100);
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 트리거하면 예외가 발생하고 동기화는 수행되지 않는다")
    void triggerSync_userNotFound_throwsAndSkipsSync() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.triggerSync(1L))
                .isInstanceOf(GeneralException.class);
        verifyNoInteractions(connector);
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 트리거하면 예외가 발생하고 동기화는 수행되지 않는다")
    void triggerSync_notAdmin_throwsForbiddenAndSkipsSync() {
        // given
        User user = User.builder().userId(2L).name("user").role(Role.USER).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> service.triggerSync(2L))
                .isInstanceOf(GeneralException.class);
        verifyNoInteractions(connector);
    }

    @Test
    @DisplayName("새 전시가 있으면 트랜잭션 커밋 후 신규 건수를 담은 이벤트를 발행한다")
    void syncAndNotify_publishesEvent_whenNewExhibitsExist() {
        // given
        CanonicalExhibit c = canonical("200", "이벤트 전시");
        when(connector.sourceCode()).thenReturn(SRC);
        when(connector.fetchPage(1, 100)).thenReturn(new CanonicalPage(List.of(c), 1));
        when(exhibitSyncRepository.findBySourceAndExternalIdIn(eq(SRC), any())).thenReturn(List.of());
        when(exhibitHallRepository.findByNameIn(any())).thenReturn(List.of());
        when(connector.fetchDetail("200")).thenReturn(detail());
        ExhibitHall newHall = ExhibitHall.of(21L, "국립중앙박물관", "한국", "서울", true);
        when(exhibitHallRepository.save(any(ExhibitHall.class))).thenReturn(newHall);
        when(exhibitRepository.save(any(Exhibit.class)))
                .thenReturn(Exhibit.of(201L, "이벤트 전시", newHall, Status.ONGOING, c.startDate(), c.endDate()));

        // when
        service.syncAndNotify();

        // then
        ArgumentCaptor<ExhibitSyncedEvent> captor = ArgumentCaptor.forClass(ExhibitSyncedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().newCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("신규 전시가 없으면 이벤트를 발행하지 않는다")
    void syncAndNotify_doesNotPublish_whenNoNewExhibits() {
        // given
        when(connector.sourceCode()).thenReturn(SRC);
        when(connector.fetchPage(1, 100)).thenReturn(new CanonicalPage(List.of(), 0));

        // when
        service.syncAndNotify();

        // then
        verifyNoInteractions(eventPublisher);
    }

}
