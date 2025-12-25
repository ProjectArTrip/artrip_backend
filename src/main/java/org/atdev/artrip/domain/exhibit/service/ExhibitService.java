package org.atdev.artrip.domain.exhibit.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.reponse.ExhibitDetailResponse;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.home.converter.HomeConverter;
import org.atdev.artrip.global.apipayload.code.status.ExhibitError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.global.s3.web.dto.request.ImageResizeRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final HomeConverter homeConverter;
    private final S3Service s3Service;


    public ExhibitDetailResponse getExhibitDetail(Long exhibitId, ImageResizeRequest resize) {

        Exhibit exhibit = exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        String resizedPosterUrl = s3Service.buildResizeUrl(exhibit.getPosterUrl(), resize.getW(), resize.getH(), resize.getF());

        return homeConverter.toHomeExhibitResponse(exhibit, resizedPosterUrl);
    }


}
