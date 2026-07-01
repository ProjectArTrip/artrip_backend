package org.atdev.artrip.service;

import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.CurationRepository;
import org.atdev.artrip.service.dto.condition.CurationSearchCondition;
import org.atdev.artrip.utils.CursorPagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurationServiceTest {

    @Mock
    private CurationRepository curationRepository;

    @InjectMocks
    private CurationService curationService;

    private Curation curation;
    private ExhibitHall domesticHall;

    @BeforeEach
    void setCuration() {
        curation = Curation.of(
                "봄 큐레이션",
                RefreshCycle.MONTHLY,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10),
                true,
                SortType.LATEST
        );

        ReflectionTestUtils.setField(curation, "curationId", 1L);

        domesticHall = ExhibitHall.of(1L, "서울 전시관", "대한민국", "서울", true);
    }

    @Test
    @DisplayName("비활성화 된 큐레이션 조회 시 예외")
    void curationDetail_inactiveCuration_throwsException() {
        //given
        Long user = 1L;
        Curation curation = Curation.of(
                "비활성화 된 큐레이션",
                RefreshCycle.MONTHLY,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10),
                false,
                SortType.LATEST
        );
        ReflectionTestUtils.setField(curation, "curationId", 99L);

        when(curationRepository.findById(curation.getCurationId())).thenReturn(Optional.of(curation));

        //when
        //then
        assertThrows(GeneralException.class, () -> curationService.getCurationDetail(user, curation.getCurationId(), new CursorPagination(null, 20L)));
    }

    @Test
    @DisplayName("존재하지 않는 큐레이션 상세 조회 시 예외")
    void curationDetail_notFound_throws() {
        // given
        Long curationId = 999L;
        when(curationRepository.findById(curationId)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(GeneralException.class,
                () -> curationService.getCurationDetail(1L, curationId, new CursorPagination(null, 20L)));
    }

    @Test
    @DisplayName("국내 필터와 country 같이 조회할 경우 예외")
    void summary_domesticAndCountryTogether_throws() {
        // given
        CurationSearchCondition condition = new CurationSearchCondition(true, "프랑스");

        // when
        // then
        assertThrows(GeneralException.class, () -> curationService.getSummaryCuration(1L, condition));
    }
}
