package org.atdev.artrip.service;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.search.SearchHistory;
import org.atdev.artrip.repository.SearchHistoryRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.SearchHistoryCommand;
import org.atdev.artrip.service.dto.result.SearchHistoryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
public class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SearchHistoryService historyService;

    @Mock
    private User testUser;

    private SearchHistory testSearchHistory;

    @BeforeEach
    void searchHistoryInfos() {
        SocialUserInfo socialUserInfo = new SocialUserInfo(
                "test@test.com",
                "테스트 유저",
                "12345555",
                Provider.KAKAO
        );

        testUser = User.of(socialUserInfo);
        ReflectionTestUtils.setField(testUser, "userId", 1L);

        testSearchHistory = SearchHistory.of(1L, testUser, "VR/AR", LocalDate.now());
    }

    @Test
    @DisplayName("검색어 저장")
    public void saveSearchHistory() {
        //given
        SearchHistoryCommand command = new SearchHistoryCommand(
                1L,
                1L,
                "나의 그림"
        );


        when(userRepository.findByUserId(command.userId())).thenReturn(Optional.of(testUser));

        //when
        assertDoesNotThrow(() -> historyService.saveSearchHistory(command));

        //then
        assertAll(
                () -> assertDoesNotThrow(() -> historyService.saveSearchHistory(command))
        );
    }

    @Test
    @DisplayName("검색어 공백일 경우 저장하지 않음")
    public void saveSearchHistory_contentBlank_notSave() {
        //given
        SearchHistoryCommand command = new SearchHistoryCommand(
                1L,
                1L,
                "   "
        );

        when(userRepository.findByUserId(command.userId())).thenReturn(Optional.of(testUser));

        //when
        historyService.saveSearchHistory(command);

        //then
        assertAll(
                () -> verify(searchHistoryRepository, never()).deleteDuplicate(anyLong(), anyString()),
                () -> verify(searchHistoryRepository, never()).save(any())
        );
    }

    @Test
    @DisplayName("최근 검색어 조회")
    public void getRecentSearchHistory() {
        //given
        Long userId = 1L;
        SearchHistoryCommand command = SearchHistoryCommand.from(userId);

        SearchHistory history = SearchHistory.of(2L, testUser, "과학적 미술", LocalDate.now());
        List<SearchHistory> histories = List.of(testSearchHistory,history);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(searchHistoryRepository.findRecent(userId)).thenReturn(histories);

        //when
        List<SearchHistoryResult> results = historyService.getRecentSearchHistory(command);

        //then
        assertAll(
                () -> assertThat(results).hasSize(2),
                () -> assertThat(results.get(0).content()).isEqualTo("VR/AR"),
                () -> assertThat(results.get(1).content()).isEqualTo("과학적 미술")
        );
    }

    @Test
    @DisplayName("검색어 id를 통해 삭제")
    public void deleteSearchHistory() {
        // given
        Long userId = 1L;
        Long searchHistoryId = 1L;
        SearchHistoryCommand command = SearchHistoryCommand.of(userId, searchHistoryId);


        when(searchHistoryRepository.findById(searchHistoryId))
                .thenReturn(Optional.of(testSearchHistory));

        // when
        assertDoesNotThrow(() -> historyService.deleteSearchHistory(command));

        // then
        verify(searchHistoryRepository).delete(testSearchHistory);
    }
}
