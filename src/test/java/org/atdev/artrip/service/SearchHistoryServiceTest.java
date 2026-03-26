package org.atdev.artrip.service;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.controller.dto.response.SocialUserInfo;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.search.SearchHistory;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
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
    private SearchHistory testSearchHistory1;
    private SearchHistory testSearchHistory2;

    @BeforeEach
    void searchHistoryInfos() {
        SocialUserInfo socialUserInfo = new SocialUserInfo(
                "test@test.com",
                "테스트 유저",
                "12345555",
                Provider.KAKAO
        );

        testUser = User.createUser(socialUserInfo);
        ReflectionTestUtils.setField(testUser, "userId", 1L);

        testSearchHistory = SearchHistory.create(testUser, "11");
        testSearchHistory1 = SearchHistory.create(testUser, "10");
        testSearchHistory2 = SearchHistory.create(testUser, "9");
    }

    @Test
    @DisplayName("검색어 공백일 경우 저장하지 않음")
    public void saveSearchHistory_contentBlank_notSave() {
        //given
        SearchHistoryCommand command = SearchHistoryCommand.create(1L, "    ");

        when(userRepository.findByUserId(command.userId())).thenReturn(Optional.of(testUser));

        //when
        historyService.saveSearchHistory(command);

        //then
        assertAll(
                () -> verify(searchHistoryRepository, never()).deleteDuplicate(anyLong(), anyString()),
                () -> verify(searchHistoryRepository, never()).save(any(SearchHistory.class)),
                () -> verify(searchHistoryRepository, never()).countByUser_UserId(anyLong()),
                () -> verify(searchHistoryRepository, never()).delete(any(SearchHistory.class))
        );
    }

    @Test
    @DisplayName("검색어 trim으로 가공 후 저장")
    void saveSearchHistory_contentBlank_trim() {
        //given
        SearchHistoryCommand command = SearchHistoryCommand.create(1L, " VR/AR   ");
        when(userRepository.findByUserId(command.userId())).thenReturn(Optional.of(testUser));
        when(searchHistoryRepository.countByUser_UserId(command.userId())).thenReturn(3L);

        //when
        historyService.saveSearchHistory(command);

        //then
        assertAll(
                () -> verify(searchHistoryRepository).deleteDuplicate(1L, "VR/AR"),
                () -> verify(searchHistoryRepository).save(argThat(history -> history.getContent().equals("VR/AR"))),
                () -> verify(searchHistoryRepository, never()).delete(any(SearchHistory.class))
        );
    }

    @Test
    @DisplayName("오래된 검색어만 삭제")
    void saveSearchHistory_whenOverflow_removeOnlyOldest() {
        //given
        SearchHistoryCommand command = SearchHistoryCommand.create(1L, "11");
        SearchHistory oldest = SearchHistory.create(testUser, "1");

        when(userRepository.findByUserId(command.userId())).thenReturn(Optional.of(testUser));
        when(searchHistoryRepository.countByUser_UserId(command.userId())).thenReturn(11L);
        when(searchHistoryRepository.findOldestSearchHistory(command.userId())).thenReturn(Optional.of(oldest));

        //when
        historyService.saveSearchHistory(command);

        //then
        assertAll(
                () -> verify(searchHistoryRepository).deleteDuplicate(1L, "11"),
                () -> verify(searchHistoryRepository).save(argThat(history -> history.getContent().equals("11"))),
                () -> verify(searchHistoryRepository).delete(oldest)
        );
    }

    @Test
    @DisplayName("중복단어 검색 일 경우 삭제 및 변경 사항 없음")
    void saveSearchHistory_whenDuplicate_doseNotRemoveOldest() {
        SearchHistoryCommand command = SearchHistoryCommand.create(1L, "AR/VR");
        when(userRepository.findByUserId(command.userId())).thenReturn(Optional.of(testUser));
        when(searchHistoryRepository.countByUser_UserId(command.userId())).thenReturn(10L);

        //when
        historyService.saveSearchHistory(command);

        //then
        assertAll(
                () -> verify(searchHistoryRepository).deleteDuplicate(1L, "AR/VR"),
                () -> verify(searchHistoryRepository).save(argThat(history -> history.getContent().equals("AR/VR"))),
                () -> verify(searchHistoryRepository, never()).delete(any(SearchHistory.class))
        );
    }

    @Test
    @DisplayName("생성날짜 모두 같아도 searchHistoryId기준으로 최신 검색어 순")
    void getRecentSearchHistory_whenCreatedAtIsSame_keepStableOrder() {
        //given
        Long userId = 1L;
        SearchHistoryCommand command = SearchHistoryCommand.from(userId);

        List<SearchHistory> histories = List.of(testSearchHistory, testSearchHistory1, testSearchHistory2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(searchHistoryRepository.findRecent(userId)).thenReturn(histories);

        //when
        List<SearchHistoryResult> results = historyService.getRecentSearchHistory(command);

        //then
        assertThat(results).extracting(SearchHistoryResult::content).containsExactly("11", "10", "9");
    }

    @Test
    @DisplayName("최대 길이 검색일 경우 예외 발생")
    void saveSearchHistory_ContentTooLong_throwsException() {
        //given
        SearchHistoryCommand command = SearchHistoryCommand.create(1L, "3o45u57484856858");
        when(userRepository.findByUserId(command.userId())).thenReturn(Optional.of(testUser));

        //when
        //then
        assertThrows(GeneralException.class, () -> historyService.saveSearchHistory(command));

        assertAll(
                () -> verify(searchHistoryRepository, never()).deleteDuplicate(anyLong(), anyString()),
                () -> verify(searchHistoryRepository, never()).save(any(SearchHistory.class)),
                () -> verify(searchHistoryRepository, never()).delete(any(SearchHistory.class))
        );
    }


}
