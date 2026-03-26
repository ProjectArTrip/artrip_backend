package org.atdev.artrip.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.FileFolder;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.FcmErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.repository.FavoriteRepository;
import org.atdev.artrip.repository.ReviewRepository;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.infra.s3.service.S3Service;
import org.atdev.artrip.service.dto.result.MypageResult;
import org.atdev.artrip.utils.NicknameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final UserCommandService userCommandService;
    private final FavoriteRepository favoriteRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public MypageResult updateNickName(Long userId, String newNickName){

        User user = findUserOrThrow(userId);

        if (newNickName.equals(user.getNickName())||userRepository.existsByNickName(newNickName)) {
            throw new GeneralException(UserErrorCode._DUPLICATE_NICKNAME);
        }

        String validatedNickName = NicknameUtils.getValidatedNickname(newNickName);

        user.updateNickName(validatedNickName);
        return new MypageResult(user.getNickName(), user.getProfileImageUrl(), user.getEmail());
    }

    public MypageResult updateUserImage(Long userId, MultipartFile image){

        if (image == null || image.isEmpty()) {
            throw new GeneralException(UserErrorCode._PROFILE_IMAGE_NOT_EXIST);
        }

        String newUrl = s3Service.uploadFile(image, FileFolder.PROFILES);
        User user = findUserOrThrow(userId);

        try {
            String oldUrl = userCommandService.updateProfilePath(userId, newUrl);

            if (oldUrl != null && !oldUrl.isBlank()) {
                s3Service.delete(oldUrl);
            }
        } catch (Exception e) {
            s3Service.delete(newUrl);
            throw e;
        }

        return new MypageResult(user.getNickName(), user.getProfileImageUrl(), user.getEmail());
    }

    public void deleteUserImage(Long userId){

        String oldUrl = userCommandService.deleteProfilePath(userId);

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

    @Transactional
    public void updateFcmToken(Long userId, String token) {

        User user = findUserOrThrow(userId);

        if (token == null || token.isBlank()) {
            throw new GeneralException(FcmErrorCode._INVALID_REQUEST_PATTERN);
        }

        String trimmedToken = token.trim();

        if(trimmedToken.equals(user.getFcmToken())) {
            return;
        }

        userRepository.findByFcmToken(trimmedToken).ifPresent(otherUser -> {
            if (!otherUser.getUserId().equals(userId)) {
                otherUser.clearFcmToken();
            }
        });

        user.updateFcmToken(trimmedToken);
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = findUserOrThrow(userId);

        try {
            favoriteRepository.deleteAllByUser(user);
            userKeywordRepository.deleteAllByUser(user);
            reviewRepository.deleteAllByUser(user);
            userRepository.delete(user);
        } catch (Exception e) {
            throw new GeneralException(UserErrorCode._WITHDRAW_FAILED);
        }
    }

}
