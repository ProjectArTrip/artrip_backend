package org.atdev.artrip.service;

import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.global.apipayload.code.status.CurationErrorCode;
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

import static org.assertj.core.api.Assertions.assertThat;
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
                "벚꽃 전시",
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
    @DisplayName("허용되지 않은 country 문자열 입력시 예외")
    void summaryCuration_invalidCountry_throws() {
        //given
        CurationSearchCondition condition = new CurationSearchCondition(
                false,
                "대한민국",
                null
        );

        //when
        //then
        assertThrows(GeneralException.class, () -> curationService.getSummaryCuration(1L, condition));
    }

    @Test
    @DisplayName("국가 조회시 enum 라벨에 없으면 예외")
    void summaryCuration_invalidCountryLabel_throws() {
        // given
        CurationSearchCondition condition = new CurationSearchCondition(null, null, "지구");

        //when
        //then
        GeneralException ex = assertThrows(GeneralException.class, () -> curationService.getSummaryCuration(1L, condition));
        assertThat(ex.getCode()).isEqualTo(CurationErrorCode._INVALID_COUNTRY);
    }

    @Test
    @DisplayName("국내 조회시 country 입력값 있으면 예외")
    void summaryCuration_domesticTrueWithCountry_throws() {
        // given
        CurationSearchCondition condition = new CurationSearchCondition(true, null, "프랑스");

        //when
        //then
        GeneralException ex = assertThrows(GeneralException.class, () -> curationService.getSummaryCuration(1L, condition));
        assertThat(ex.getCode()).isEqualTo(CurationErrorCode._INVALID_LOCATION_FILTER);
    }

    @Test
    @DisplayName("해외 조회 시 region 입력값 있으면 예외")
    void summaryCuration_domesticFalseWithRegion_throws() {
        //given
        CurationSearchCondition condition = new CurationSearchCondition(false, "서울", null);

        //when
        //then
        GeneralException ex = assertThrows(GeneralException.class, () -> curationService.getSummaryCuration(1L, condition));
        assertThat(ex.getCode()).isEqualTo(CurationErrorCode._INVALID_LOCATION_FILTER);
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
                SortType.LATEST
        );
        ReflectionTestUtils.setField(curation, "curationId", 99L);

        when(curationRepository.findById(curation.getCurationId())).thenReturn(Optional.of(curation));

        //when
        //then
        assertThrows(GeneralException.class, () -> curationService.getCurationDetail(user, curation.getCurationId(), new CursorPagination(null, 20L)));
    }
}
