package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.review.Review;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.ReviewRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.ReviewCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewLogicService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;

    private User findUserById(Long userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
    }

    private Exhibit findExhibitById(Long exhibitId) {
        return exhibitRepository.findById(exhibitId)
                .orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));
    }


    @Transactional
    public void saveReviewWithImages(ReviewCommand command, List<String> s3Urls) {

        User user = findUserById(command.userId());
        Exhibit exhibit = findExhibitById(command.exhibitId());

        Review review = command.toEntity(user, exhibit);

        if (s3Urls != null && !s3Urls.isEmpty()) {
            review.addImages(s3Urls);
        }

        reviewRepository.save(review);
    }

}
