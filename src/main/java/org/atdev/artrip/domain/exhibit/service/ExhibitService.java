package org.atdev.artrip.domain.exhibit.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.reponse.ExhibitDetailResponse;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.favortie.repository.FavoriteExhibitRepository;
import org.atdev.artrip.domain.home.converter.HomeConverter;
import org.atdev.artrip.global.apipayload.code.status.ExhibitError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final HomeConverter homeConverter;
    private final FavoriteExhibitRepository favoriteExhibitRepository;


    public ExhibitDetailResponse getExhibitDetail(Long exhibitId, Long userId) {

        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        boolean isFavorite = false;
        if (userId != null ) {
            isFavorite = favoriteExhibitRepository.existsActive(userId, exhibitId);
        }

        return homeConverter.toHomeExhibitResponse(exhibit, isFavorite);
    }


}
