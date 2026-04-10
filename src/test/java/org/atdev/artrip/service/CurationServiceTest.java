package org.atdev.artrip.service;

import org.atdev.artrip.constants.Country;
import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.curation.CurationExhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.CurationRepository;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.service.dto.result.CurationDetailCursorResult;
import org.atdev.artrip.service.dto.result.CurationSummaryListResult;
import org.atdev.artrip.utils.CursorPagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurationServiceTest {

    @Mock
    private CurationRepository curationRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private CurationService curationService;

    private Curation curation;
    private ExhibitHall domesticHall;
    private ExhibitHall foreignHall;

    @BeforeEach
    void setCuration() {
        curation = Curation.of(
                "봄 큐레이션",
                "벚꽃 전시",
                RefreshCycle.MONTHLY,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10),
                true,
                SortType.LATEST,
                3
        );

        ReflectionTestUtils.setField(curation, "curationId", 1L);

        domesticHall = ExhibitHall.of(1L, "서울 전시관", "대한민국", "서울", true);
        foreignHall = ExhibitHall.of(2L, "프랑스 전시관", "프랑스", "파리", false);
    }

    @Test
    @DisplayName("특정 국가 조회 시 필터 후 남는 전시 없으면 제외")
    void summaryCuration_filtersExhibitsByCountry() {
        //given
        Long user = 1L;
        when(curationRepository.findVisibleCurationsByCountry(Country.GERMANY, LocalDate.now())).thenReturn(List.of(curation));
        when(favoriteRepository.findActiveExhibitIds(user)).thenReturn(Set.of());

        //when
        CurationSummaryListResult result = curationService.getSummaryCuration(user, Country.GERMANY);

        //then
        assertThat(result.curations()).isEmpty();
    }

    @Test
    @DisplayName("없는 큐레이션 조회 시 예외")
    void curationDetail_curationId_throwsException() {
        //given
        Long user = 1L;
        Long curationsId = 999L;
        when(curationRepository.findById(curationsId)).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(GeneralException.class, () -> curationService.getCurationDetail(user, curationsId, new CursorPagination(null, 20L)));
    }

    @Test
    @DisplayName("비활성화 된 큐레이션 조회 시 예외")
    void curationDetail_inactiveCuration_throwsException() {
        //given
        Long user = 1L;
        Curation curation = Curation.of(
                "비활성화 된 큐레이션",
                "서어브 타이틀",
                RefreshCycle.MONTHLY,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10),
                false,
                SortType.LATEST,
                3
        );
        ReflectionTestUtils.setField(curation, "curationId", 99L);

        when(curationRepository.findById(curation.getCurationId())).thenReturn(Optional.of(curation));

        //when
        //then
        assertThrows(GeneralException.class, () -> curationService.getCurationDetail(user, curation.getCurationId(), new CursorPagination(null, 20L)));
    }

    @Test
    @DisplayName("활성화 된 큐레이션 일 경우 연결 된 전시가 없을 경우 빈값 처리")
    void curationDetail_noExhibit_returnEmptyList() {
        //given
        Long user = 1L;
        Slice<CurationExhibit> slice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);

        when(curationRepository.findById(curation.getCurationId())).thenReturn(Optional.of(curation));
        when(curationRepository.findExhibitsByCurationId(curation.getCurationId(), PageRequest.ofSize(20))).thenReturn(slice);

        //when
        CurationDetailCursorResult result = curationService.getCurationDetail(
                user, curation.getCurationId(), new CursorPagination(null, 20L));

        //then
        assertAll(
                () -> assertThat(result.exhibits()).isEmpty(),
                () -> assertThat(result.hasNext()).isFalse(),
                () -> assertThat(result.nextCursor()).isNull()
        );
    }
}
