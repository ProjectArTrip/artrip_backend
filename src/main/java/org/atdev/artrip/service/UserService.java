package org.atdev.artrip.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.service.dto.result.ExhibitRecentResult;
import org.atdev.artrip.service.dto.result.MypageResult;
import org.atdev.artrip.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    @Qualifier("recommendRedisTemplate")
    private final StringRedisTemplate recommendRedisTemplate;
    private final ExhibitRepository exhibitRepository;
    private final TransactionTemplate transactionTemplate;

    @Transactional
    public void updateNickName(Long userId, String newNickName){

        User user = findUserOrThrow(userId);

        if (newNickName.equals(user.getNickName())) {
            return;
        }

        if (userRepository.existsByNickName(newNickName)) {
            throw new GeneralException(UserErrorCode._DUPLICATE_NICKNAME);
        }

        user.updateNickname(newNickName);
    }

    public void updateUserImage(Long userId, MultipartFile image){

        User user = findUserOrThrow(userId);


        if (image == null || image.isEmpty()) {
            throw new GeneralException(UserErrorCode._PROFILE_IMAGE_NOT_EXIST);
        }

        String oldUrl = user.getProfileImageUrl();
        String newUrl = s3Service.uploadProfile(image);

        try {
            transactionTemplate.executeWithoutResult(status -> {
                User tUser = findUserOrThrow(userId);
                tUser.updateProfileImage(newUrl);
            });
        } catch (Exception e) {
            s3Service.delete(newUrl);
            throw e;
        }

        if (oldUrl != null && !oldUrl.isBlank()) {
            s3Service.delete(oldUrl);
        }
    }

    public void deleteUserImage(Long userId){

        User user = findUserOrThrow(userId);
        String oldUrl = user.getProfileImageUrl();

        transactionTemplate.executeWithoutResult(status -> {
            User tUser = findUserOrThrow(userId);
            tUser.updateProfileImage(null);
        });

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

    public List<ExhibitRecentResult> getRecentViews(Long userId) {

        String key = RedisUtils.getRecentViewKey(userId);
        Set<String> result = recommendRedisTemplate.opsForZSet().reverseRange(key, 0, 19);
        if (result == null || result.isEmpty())
            return List.of();

        List<Long> ids= result.stream()
                .map(Long::valueOf)
                .toList();

        List<Exhibit> exhibits = exhibitRepository.findAllByIdWithHall(ids);
        if (exhibits.isEmpty()) return List.of();

        Map<Long, Exhibit> exhibitMap = exhibits.stream()
                .collect(Collectors.toMap(Exhibit::getExhibitId, e -> e));

        return ids.stream()
                .map(exhibitMap::get)
                .filter(Objects::nonNull)
                .map(ExhibitRecentResult::from)
                .toList();
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
    }
}
