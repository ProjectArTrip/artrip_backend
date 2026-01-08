package org.atdev.artrip.service;

import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.response.ExhibitSearchResponse;
import org.atdev.artrip.controller.dto.response.FilterResponse;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @InjectMocks
    SearchService searchService;

    @Mock
    ExhibitRepository exhibitRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FavoriteExhibitRepository favoriteExhibitRepository;


    @DisplayName("키워드 전시 검색 결과 조회")
    @Test
    void searchKeyword() {

        // Given
        String keyword = "현대";
        Long userId = 1L;
        ImageResizeRequest resized = new ImageResizeRequest(100, 100, "webp");
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
                .build();

        List<Exhibit> content = List.of(mockExhibit);
        Slice<Exhibit> slice = new SliceImpl<>(content, PageRequest.of(0, 10), false);

        ExhibitSearchResponse mockDto = ExhibitSearchResponse.builder()
                .id(1L)
                .title("Matisse – Soulages")
                .posterUrl(mockResizedUrl)
                .build();

        when(exhibitRepository.searchByKeyword(anyString(), any(), anyLong()))
                .thenReturn(slice);
        when(modelMapper.map(any(Exhibit.class), eq(ExhibitSearchResponse.class)))
                .thenReturn(mockDto);
        when(s3Service.buildResizeUrl(any(),any(),any(),any()))
                .thenReturn(mockResizedUrl);
        when(favoriteExhibitRepository.findActiveExhibitIds(anyLong()))
                .thenReturn(Collections.emptySet());

        // when
        FilterResponse<ExhibitSearchResponse> result = searchService.getKeyword(keyword, null, 10L, userId, resized);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getResult().size());
        ExhibitSearchResponse actualDto = result.getResult().get(0);
        Assertions.assertEquals("Matisse – Soulages", actualDto.getTitle());
        Assertions.assertEquals(mockResizedUrl, actualDto.getPosterUrl());

    }

}
