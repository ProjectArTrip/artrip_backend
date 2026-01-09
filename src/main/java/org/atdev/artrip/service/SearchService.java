package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.controller.dto.response.ExhibitSearchResponse;
import org.atdev.artrip.controller.dto.response.FilterResponse;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class SearchService {

    private final ExhibitRepository exhibitRepository;
    private final ModelMapper modelMapper;
    private final FavoriteExhibitRepository favoriteExhibitRepository;
    private final S3Service s3Service;

    private Set<Long> getFavoriteIds(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        return favoriteExhibitRepository.findActiveExhibitIds(userId);
    }

    @Transactional(readOnly = true)
    public FilterResponse<ExhibitSearchResponse> getKeyword(String keyword, Long cursor, Long size, Long userId, ImageResizeRequest resize) {

        Slice<Exhibit> slice = exhibitRepository.searchByKeyword(keyword, cursor, size);
        Set<Long> favoriteIds = getFavoriteIds(userId);

        List<ExhibitSearchResponse> result = slice.getContent().stream()
                .map(exhibit -> {
                    ExhibitSearchResponse dto = modelMapper.map(exhibit, ExhibitSearchResponse.class);

                    String resizeUrl = s3Service.buildResizeUrl(exhibit.getPosterUrl(), resize.getW(), resize.getH(), resize.getF());
                    if (exhibit.getExhibitHall() != null) {
                        ExhibitHall hall = exhibit.getExhibitHall();

                        if (Boolean.TRUE.equals(hall.getIsDomestic())) {
                            dto.setLocation(hall.getRegion());
                        } else {
                            dto.setLocation(hall.getCountry());
                        }
                    }

                    dto.setExhibitHallName(exhibit.getExhibitHall().getName());
                    dto.setPosterUrl(resizeUrl);
                    dto.setIsFavorite(favoriteIds.contains(exhibit.getExhibitId()));
                    return dto;
                }).toList();

        Long nextCursor = (slice.hasNext() && !result.isEmpty()) ? slice.getContent().get(slice.getContent().size() - 1 ).getExhibitId() : null ;

        return new FilterResponse<>(result, slice.hasNext(), nextCursor);
    }
}
