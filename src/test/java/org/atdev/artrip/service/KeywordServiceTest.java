package org.atdev.artrip.service;

import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.atdev.artrip.global.apipayload.code.status.KeywordErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.KeywordRepository;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.KeywordCommand;
import org.atdev.artrip.service.dto.result.KeywordResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {

    @InjectMocks
    private KeywordService keywordService;
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private UserKeywordRepository userKeywordRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("키워드 저장 성공")
    void saveKeywords_success() {

        // given
        Long userId = 1L;
        User user = mock(User.class);

        Keyword k1 = Keyword.builder()
                .type(KeywordType.GENRE)
                .name("전시")
                .build();

        Keyword k2 = Keyword.builder()
                .type(KeywordType.GENRE)
                .name("미술")
                .build();

        KeywordCommand command = new KeywordCommand(
                List.of("전시", "미술"),
                userId
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(keywordRepository.findAllByNameIn(anyList()))
                .thenReturn(List.of(k1, k2));

        // when
        keywordService.saveKeywords(command);

        // then
        verify(userKeywordRepository).deleteByUserId(userId);
        verify(userKeywordRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("존재하지 않는 유저면 예외 발생")
    void saveKeywords_userNotFound() {

        // given
        KeywordCommand command = new KeywordCommand(
                List.of("전시"),
                99L
        );

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> keywordService.saveKeywords(command))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(UserErrorCode._USER_NOT_FOUND);
    }


    @Test
    @DisplayName("존재하지 않는 키워드 포함 시 예외 발생")
    void saveKeywords_keywordNotFound() {

        // given
        Long userId = 1L;
        User user = mock(User.class);

        KeywordCommand command = new KeywordCommand(
                List.of("전시", "없는키워드"),
                userId
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(keywordRepository.findAllByNameIn(anyList()))
                .thenReturn(List.of(
                        Keyword.builder()
                                .keywordId(1L)
                                .type(KeywordType.GENRE)
                                .name("전시")
                                .build()
                ));

        // when / then
        assertThatThrownBy(() -> keywordService.saveKeywords(command))
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(KeywordErrorCode._KEYWORD_NOT_FOUND);

    }

    @Test
    @DisplayName("전체 키워드 조회")
    void getAllKeywords_success() {
        // given
        Keyword k1 = Keyword.builder()
                .type(KeywordType.GENRE)
                .name("전시")
                .build();

        Keyword k2 = Keyword.builder()
                .type(KeywordType.GENRE)
                .name("미술")
                .build();

        when(keywordRepository.findAll())
                .thenReturn(List.of(
                        k1,k2
                ));

        // when
        List<KeywordResult> results = keywordService.getAllKeywords();

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("유저의 키워드 조회")
    void getKeywordByUser_success() {

        // given
        Long userId = 1L;

        Keyword k1 = Keyword.builder()
                .type(KeywordType.GENRE)
                .name("전시")
                .build();

        Keyword k2 = Keyword.builder()
                .type(KeywordType.GENRE)
                .name("미술")
                .build();

        UserKeyword uk1 = mock(UserKeyword.class);
        UserKeyword uk2 = mock(UserKeyword.class);

        when(uk1.getKeyword()).thenReturn(k1);
        when(uk2.getKeyword()).thenReturn(k2);

        when(userKeywordRepository.findAllByUserIdWithKeyword(userId))
                .thenReturn(List.of(uk1, uk2));

        // when
        List<KeywordResult> results = keywordService.getKeyword(userId);

        // then
        assertThat(results).hasSize(2);

        assertThat(results)
                .extracting(KeywordResult::name)
                .containsExactlyInAnyOrder("전시", "미술");
    }

}
