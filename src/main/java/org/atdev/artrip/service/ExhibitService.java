package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.global.s3.util.ImageUrlFormatter;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.atdev.artrip.converter.HomeConverter;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.dto.ExhibitDetailResult;
import org.atdev.artrip.service.dto.ExhibitDetailQuery;
import org.atdev.artrip.service.redis.UserHistoryRedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final FavoriteExhibitRepository favoriteExhibitRepository;
    private final ImageUrlFormatter imageUrlFormatter;
    private final UserHistoryService userHistoryService;


    @Transactional(readOnly = true)
    public ExhibitDetailResult getExhibitDetail(ExhibitDetailQuery query) {

        Exhibit exhibit = exhibitRepository.findById(query.exhibitId())
                .orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));

        String resizedPosterUrl = imageUrlFormatter.getResizedUrl(
                exhibit.getPosterUrl(),
                query.width(),
                query.height(),
                query.format()
        );

        boolean isFavorite = false;
        if (query.userId() != null) {
            isFavorite = favoriteExhibitRepository.existsActive(query.userId(), query.exhibitId());
            userHistoryService.addRecentView(query.userId(), query.exhibitId());
        }

        return new ExhibitDetailResult(exhibit, isFavorite, resizedPosterUrl);
    }


}
