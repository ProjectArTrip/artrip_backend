package org.atdev.artrip.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.service.dto.command.UserReadCommand;
import org.atdev.artrip.service.dto.command.NicknameCommand;
import org.atdev.artrip.service.dto.command.ProfileCommand;
import org.atdev.artrip.service.dto.result.ExhibitRecentResult;
import org.atdev.artrip.service.dto.result.MypageResult;
import org.atdev.artrip.service.dto.result.NicknameResult;
import org.atdev.artrip.service.dto.result.ProfileResult;
import org.atdev.artrip.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
    public NicknameResult updateNickName(NicknameCommand command){

        User user = findUserOrThrow(command.userId());
        String newNick = command.nickName();

        if (newNick.equals(user.getNickName())) {
            return new NicknameResult(newNick);
        }

        if (userRepository.existsByNickName(newNick)) {
            throw new GeneralException(UserErrorCode._DUPLICATE_NICKNAME);
        }

        user.updateNickname(newNick);
        return new NicknameResult(newNick);
    }

    public ProfileResult updateProfileImg(ProfileCommand command){

        User user = findUserOrThrow(command.userId());


        if (command.image() == null || command.image().isEmpty()) {
            throw new GeneralException(UserErrorCode._PROFILE_IMAGE_NOT_EXIST);
        }

        String oldUrl = user.getProfileImageUrl();
        String newUrl = s3Service.uploadProfile(command.image());

        try {
            transactionTemplate.executeWithoutResult(status -> {
                User tUser = findUserOrThrow(command.userId());
                tUser.updateProfileImage(newUrl);
            });
        } catch (Exception e) {
            s3Service.delete(newUrl);
            throw e;
        }

        if (oldUrl != null && !oldUrl.isBlank()) {
            s3Service.delete(oldUrl);
        }

        return new ProfileResult(newUrl);
    }

    public void deleteProfileImg(ProfileCommand command){

        User user = findUserOrThrow(command.userId());


        String oldUrl = user.getProfileImageUrl();

        transactionTemplate.executeWithoutResult(status -> {
            User tUser = findUserOrThrow(command.userId());
            tUser.updateProfileImage(null);
        });

        if (oldUrl != null && !oldUrl.isBlank()) {
                s3Service.delete(oldUrl);
        }
    }

    @Transactional(readOnly = true)
    public MypageResult getMypage(UserReadCommand command){

        User user = findUserOrThrow(command.userId());
        String profileImage = user.getProfileImageUrl();

        return new MypageResult(user.getNickName(), profileImage, user.getEmail());
    }

    public List<ExhibitRecentResult> getRecentViews(UserReadCommand command) {

        String key = RedisUtils.getRecentViewKey(command.userId());
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
