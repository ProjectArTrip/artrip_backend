package org.atdev.artrip.service;

import org.atdev.artrip.service.dto.command.ExhibitFilterCommand;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
public class ExhibitSearchFacadeTest {

    @Mock
    private HomeService homeService;

    @Mock
    private SearchHistoryService searchHistoryService;

    @InjectMocks
    private ExhibitSearchFacade exhibitSearchFacade;

    @Test
    @DisplayName("userId와 query가 있을 경우 검색 후 저장")
    void searchAndSaveHistory_withUserIdAndQuery() {
        Long userId = 1L;
        String query = "인터랙티브";
        ExhibitFilterCommand command = ExhibitFilterCommand.builder()
                .userId(userId)
                .query(query)
                .size(10L)
                .build();

        ExhibitFilterResult mockResult = new ExhibitFilterResult(Collections.emptyList(), false, null);
        when(homeService.getFilterExhibit(any())).thenReturn(mockResult);

        ExhibitFilterResult result = exhibitSearchFacade.searchAndSaveHistory(command);

        assertThat(result).isEqualTo(mockResult);
        verify(homeService).getFilterExhibit(command);
        verify(searchHistoryService).saveSearchHistory(userId, query);
    }

    @Test
    @DisplayName("userId가 null이면 저장 안함")
    void searchAndSaveHistory_withoutUserId() {
        ExhibitFilterCommand command = ExhibitFilterCommand.builder()
                .userId(null)
                .query("순수 미술")
                .size(10L)
                .build();

        ExhibitFilterResult mockResult = new ExhibitFilterResult(Collections.emptyList(), false, null);
        when(homeService.getFilterExhibit(any())).thenReturn(mockResult);

        exhibitSearchFacade.searchAndSaveHistory(command);

        verify(homeService).getFilterExhibit(command);
        verify(searchHistoryService, never()).saveSearchHistory(any(), any());
    }
}
