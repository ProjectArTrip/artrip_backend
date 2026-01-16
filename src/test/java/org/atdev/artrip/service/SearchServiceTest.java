package org.atdev.artrip.service;

import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.response.CursorPaginationResponse;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @InjectMocks
    HomeService homeService;

    @Mock
    ExhibitRepository exhibitRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private FavoriteExhibitRepository favoriteExhibitRepository;

    @DisplayName("키워드 전시 검색 결과 조회")
    @Test
    void searchKeyword() {

        // Given
        ExhibitFilterRequest request = new ExhibitFilterRequest();
        request.setKeyword("현대 전시");

        Long userId = 1L;
        ImageResizeRequest resizeRequest = new ImageResizeRequest(100, 100, "webp");
        String mockResizedUrl = "https://resized-url.com?w=100&h100";

        ExhibitHall mockExhibitHall = ExhibitHall.builder()
                .name("Hangar Y")
                .isDomestic(false)
                .region("뮈동")
                .build();

        Exhibit mockExhibit = Exhibit.builder()
                .exhibitId(1L)
                .title("Matisse – Soulages")
                .posterUrl(mockResizedUrl)
                .exhibitHall(mockExhibitHall)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .build();

        List<Exhibit> content = List.of(mockExhibit);
        Slice<Exhibit> slice = new SliceImpl<>(content, PageRequest.of(0, 10), false);

        when(exhibitRepository.findExhibitByFilters(any(), any(), any()))
                .thenReturn(slice);
        when(s3Service.buildResizeUrl(any(),any(),any(),any()))
                .thenReturn(mockResizedUrl);
        when(favoriteExhibitRepository.findActiveExhibitIds(anyLong()))
                .thenReturn(Collections.emptySet());

        // when
        CursorPaginationResponse<HomeListResponse> result = homeService.findExhibits(request, resizeRequest,  userId);

        // then
        HomeListResponse actualDto = result.getData().get(0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getData().size());
        Assertions.assertEquals("Matisse – Soulages", actualDto.getTitle());
        Assertions.assertEquals(mockResizedUrl, actualDto.getPosterUrl());

    }

}
