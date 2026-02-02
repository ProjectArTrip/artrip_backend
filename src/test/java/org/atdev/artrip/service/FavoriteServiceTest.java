package org.atdev.artrip.service;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.FavoriteCondition;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private User testUser;
    private ExhibitHall domesticHall;
    private ExhibitHall overseasHall;
    private Exhibit ongoingExhibit;
    private Exhibit endingSoonExhibit;
    private Favorite firstFavorite;
    private Favorite secondFavorite;

    @BeforeEach
    void favoriteExhibitInfos() {
        SocialUserInfo socialUserInfo = new SocialUserInfo(
                "test@test.com",
                "testUser",
                "123444",
                Provider.KAKAO
        );

        testUser = User.of(socialUserInfo);

        domesticHall = ExhibitHall.of(
                1L,
                "은수 공간",
                "한국",
                "서울",
                true
        );

        overseasHall = ExhibitHall.of(
                2L,
                "루브르 박물관",
                "프랑스",
                "파리",
                false
        );

        ongoingExhibit = Exhibit.of(
                1L,
                "일차(근본적이고 원초적인 것)",
                domesticHall,
                Status.ONGOING,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(30)
        );

        endingSoonExhibit = Exhibit.of(
                2L,
                "모나리자스폐셜",
                overseasHall,
                Status.ONGOING,
                LocalDate.now().minusDays(20),
                LocalDate.now().plusDays(2)
        );

        firstFavorite = new Favorite(
                1L,
                testUser,
                true,
                ongoingExhibit,
                LocalDate.now().minusDays(5)
        );

        secondFavorite = new Favorite(
                2L,
                testUser,
                true,
                endingSoonExhibit,
                LocalDate.now().minusDays(3)
        );
    }

    @Test
    @DisplayName("해외 필터 검색")
    public void getFavorite_overseas(){
        //given
        Long userId = 1L;

        ExhibitHall hall = ExhibitHall.of(6L, "루브르", "프랑스", "파리", false);
        Exhibit exhibit = Exhibit.of(6L, "프랑스 전시", hall, Status.ONGOING, LocalDate.now().minusDays(5), LocalDate.now().plusDays(10));
        Favorite favorite = new Favorite(6L, testUser, true, exhibit, LocalDate.now().minusDays(1));

        FavoriteCondition condition = FavoriteCondition.builder()
                .userId(userId)
                .sortType(SortType.LATEST)
                .isDomestic(false)
                .country("프랑")
                .region("파리")
                .cursor(null)
                .size(20L)
                .build();

        List<Favorite> favorites = List.of(favorite);
        SliceImpl<Favorite> slice = new SliceImpl<>(favorites, PageRequest.ofSize(20), false);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(favoriteRepository.findOverseasByCountry(
                eq(userId),
                eq("프랑"),
                eq("파리"),
                eq(false),
                isNull(),
                eq(Status.FINISHED),
                any(Pageable.class))).thenReturn(slice);

        //when
        FavoriteResult result = assertDoesNotThrow(() ->
                favoriteService.getFavorites(condition));

        //then
        assertAll(
                () -> assertThat(result.items().get(0).country()).contains("프")
        );
    }

    @Test
    @DisplayName("지역필터 조회 시 isDomestic 없을 시 예외")
    public void getFavorite_region_filter_empty_isDomestic() {
        //given
        //when
        //then
        assertThrows(GeneralException.class, () -> {
            FavoriteCondition.builder()
                    .userId(1L)
                    .region("경기")
                    .isDomestic(null)
                    .sortType(SortType.LATEST)
                    .size(20L)
                    .build();
        });
    }

    @Test
    @DisplayName("국내 조회 필드에 country 입력 시 예외")
    public void getFavorite_domesticField_searchDomestic() {
        //given
        //when
        //then
        assertThrows(GeneralException.class, () ->
                FavoriteCondition.builder()
                        .userId(1L)
                        .sortType(SortType.LATEST)
                        .isDomestic(true)
                        .country("프랑스")
                        .size(20L)
                        .build()
                );
    }
}