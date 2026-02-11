package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;

    @Transactional
    public String updateProfilePath(Long userId, String newUrl) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        String oldUrl = user.getProfileImageUrl();
        user.updateProfileImage(newUrl);

        return oldUrl;
    }

    @Transactional
    public String deleteProfilePath(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        String oldUrl = user.getProfileImageUrl();
        if (oldUrl == null) {
            throw new GeneralException(UserErrorCode._PROFILE_IMAGE_NOT_EXIST);
        }

        user.updateProfileImage(null);
        return oldUrl;
    }
}
