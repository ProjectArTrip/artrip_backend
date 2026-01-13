package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.controller.dto.response.ExhibitDetailResponse;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.atdev.artrip.converter.HomeConverter;
import org.atdev.artrip.global.apipayload.code.status.ExhibitError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.service.redis.UserHistoryRedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final HomeConverter homeConverter;
    private final S3Service s3Service;
    private final FavoriteExhibitRepository favoriteExhibitRepository;
    private final UserHistoryRedisService userHistoryRedisService;

    @Transactional(readOnly = true)
    public ExhibitDetailResponse getExhibitDetail(Long exhibitId, Long userId, ImageResizeRequest resize) {

        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        String resizedPosterUrl = s3Service.buildResizeUrl(exhibit.getPosterUrl(), resize.getW(), resize.getH(), resize.getF());

        boolean isFavorite = false;
        if (userId != null ) {
            isFavorite = favoriteExhibitRepository.existsActive(userId, exhibitId);
        }

        userHistoryRedisService.addRecentView(userId,exhibitId);

        return homeConverter.toHomeExhibitResponse(exhibit, isFavorite, resizedPosterUrl);
    }


}
