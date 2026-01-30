package org.atdev.artrip.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.FileFolder;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.service.dto.result.MypageResult;
import org.atdev.artrip.utils.NicknameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final UserImageService userImageService;

    @Transactional
    public void updateNickName(Long userId, String newNickName){

        User user = findUserOrThrow(userId);

        if (newNickName.equals(user.getNickName())) {
            return;
        }

        if (userRepository.existsByNickName(newNickName)) {
            throw new GeneralException(UserErrorCode._DUPLICATE_NICKNAME);
        }

        NicknameUtils.getValidatedNickname(newNickName);
    }

    public void updateUserImage(Long userId, MultipartFile image){

        if (image == null || image.isEmpty()) {
            throw new GeneralException(UserErrorCode._PROFILE_IMAGE_NOT_EXIST);
        }

        String newUrl = s3Service.uploadFile(image, FileFolder.PROFILES);

        try {
            String oldUrl = userImageService.updateProfilePath(userId, newUrl);

            if (oldUrl != null && !oldUrl.isBlank()) {
                s3Service.delete(oldUrl);
            }
        } catch (Exception e) {
            s3Service.delete(newUrl);
            throw e;
        }
    }

    public void deleteUserImage(Long userId){

        String oldUrl = userImageService.deleteProfilePath(userId);

        if (oldUrl != null && !oldUrl.isBlank()) {
                s3Service.delete(oldUrl);
        }
    }

    @Transactional(readOnly = true)
    public MypageResult getMypage(Long userId){

        User user = findUserOrThrow(userId);
        String profileImage = user.getProfileImageUrl();

        return new MypageResult(user.getNickName(), profileImage, user.getEmail());
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
    }
}
