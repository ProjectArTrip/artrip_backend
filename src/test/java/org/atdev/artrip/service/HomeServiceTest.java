package org.atdev.artrip.service;

import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.FavoriteRepositoryCustom;
import org.atdev.artrip.service.dto.condition.ExhibitSearchCondition;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
public class HomeServiceTest {

    @Mock
    private ExhibitRepository exhibitRepository;

    @Mock
    private SearchHistoryService searchHistoryService;

    @Mock
    FavoriteRepositoryCustom favoriteRepositoryCustom;

    @InjectMocks
    private HomeService homeService;

    private ExhibitHall testExhibitHall;
    private Exhibit testExhibit;
    private Keyword genreKeyword;
    private Keyword styleKeyword;

    @BeforeEach
    void exhibitTestInfos() {
        genreKeyword = new Keyword(1L, KeywordType.GENRE, "현대 미술", null);
        styleKeyword = new Keyword(2L, KeywordType.STYLE, "인터렉티브", null);

        testExhibitHall = ExhibitHall.of(1L, "서울 시립 미술관", "한국", "서울", true);

        testExhibit = Exhibit.of(
                1L,
                "모네의 정원",
                testExhibitHall,
                Status.ONGOING,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        ).withKeywords(Set.of(genreKeyword, styleKeyword));
    }

    @Test
    @DisplayName("제목 검색어 입력 시 조회")
        public void searchExhibit_byTitle() {
        //given
        ExhibitSearchCondition defaultCommand = ExhibitSearchCondition.builder()
                .userId(1L)
                .query("모네")
                .size(10L)
                .build();

        ExhibitSearchCondition nullCommand = ExhibitSearchCondition.builder()
                .userId(1L)
                .query(null)
                .size(0L)
                .build();

        when(exhibitRepository.findExhibitByFilters(any(ExhibitSearchCondition.class)))
                .thenReturn(new SliceImpl<>(List.of(testExhibit)));

        // when
        ExhibitFilterResult defaultResult = assertDoesNotThrow(() -> homeService.searchExhibit(defaultCommand));

        ExhibitFilterResult nullResult = assertDoesNotThrow(() -> homeService.searchExhibit(nullCommand));

        // then
        assertAll(
                () -> assertThat(defaultResult.items()).hasSize(1),
                () -> assertThat(defaultResult.items().get(0).title()).contains("모"),
                () -> assertThat(nullResult).isNotNull(),
                () -> verify(exhibitRepository, times(2)).findExhibitByFilters(any())
        );
    }

    @Test
    @DisplayName("장르 및 스타일 필터링 조회")
    public void searchExhibit_byGenres() {
        //given
        Set<String> genreNames = Set.of(genreKeyword.getName());
        Set<String> styleNames = Set.of(styleKeyword.getName());

        ExhibitSearchCondition command = ExhibitSearchCondition.builder()
                .genres(genreNames)
                .styles(styleNames)
                .size(10L)
                .build();

        when(exhibitRepository.findExhibitByFilters(any(ExhibitSearchCondition.class)))
                .thenReturn(new SliceImpl<>(List.of(testExhibit)));

        // when
        ExhibitFilterResult result = assertDoesNotThrow(() -> homeService.searchExhibit(command));

        // then
        assertAll(
                () -> assertThat(result.items()).hasSize(1),
                () -> assertThat(command.genres()).contains(genreKeyword.getName()),
                () -> assertThat(command.styles()).contains(styleKeyword.getName()),
                () -> assertThat(command.genres().iterator().next()).contains("현"),
                () -> assertThat(command.styles().iterator().next()).contains("인터렉")
        );
    }

    @Test
    @DisplayName("국가 필터링 조회")
    public void searchExhibit_byCountry() {
        //given
        ExhibitSearchCondition command = ExhibitSearchCondition.builder()
                .country(testExhibitHall.getCountry())
                .size(10L)
                .build();

        when(exhibitRepository.findExhibitByFilters(any(ExhibitSearchCondition.class)))
                .thenReturn(new SliceImpl<>(List.of(testExhibit)));

        // when
        ExhibitFilterResult result = assertDoesNotThrow(() -> homeService.searchExhibit(command));

        //then
        assertAll(
                () -> assertThat(result.items()).hasSize(1),
                () -> assertThat(result.items().get(0).countryName()).contains("한국"),
                () -> assertThat(result.items().get(0).countryName()).doesNotContain("대한민국")
        );
    }
}
