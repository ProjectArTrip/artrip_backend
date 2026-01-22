package org.atdev.artrip.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.global.apipayload.code.status.S3ErrorCode;
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

import java.util.Comparator;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]+$";

    @Qualifier("recommendRedisTemplate")
    private final StringRedisTemplate recommendRedisTemplate;

    private final ExhibitRepository exhibitRepository;

    @Transactional
    public NicknameResult updateNickName(NicknameCommand command){

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        String newNick = validateNickname(command);

        if (newNick.equals(user.getNickName())) {
            return new NicknameResult(newNick);
        }

        if (userRepository.existsByNickName(newNick)) {
            throw new GeneralException(UserErrorCode._DUPLICATE_NICKNAME);
        }

        user.updateNickname(newNick);

        return new NicknameResult(newNick);
    }

    private String validateNickname(NicknameCommand command) {

        if (command == null || command.nickName() == null) {
            throw new GeneralException(UserErrorCode._NICKNAME_BAD_REQUEST);
        }

        String nickname = command.nickName();

        if (nickname.isBlank() || nickname.contains(" ")) {
            throw new GeneralException(UserErrorCode._NICKNAME_BAD_REQUEST);
        }

        if (!nickname.matches(NICKNAME_REGEX)) {
            throw new GeneralException(UserErrorCode._NICKNAME_BAD_REQUEST);
        }

        return nickname;
    }

    @Transactional
    public ProfileResult updateProfileImg(ProfileCommand command){

        User user = userRepository.findById(command.userId())
                .orElseThrow(()-> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        if (command.image() == null || command.image().isEmpty()) {
            throw new GeneralException(UserErrorCode._PROFILE_IMAGE_NOT_EXIST);
        }

        String oldUrl = user.getProfileImageUrl();

        String newUrl;
        try {
            newUrl = s3Service.uploadProfile(command.image());
        } catch (Exception e) {
            throw new GeneralException(S3ErrorCode._IO_EXCEPTION_UPLOAD_FILE);
        }
        user.updateProfileImage(newUrl);

        if (oldUrl != null && !oldUrl.isBlank()) {
            try {
                s3Service.delete(oldUrl);
            } catch (Exception e) {
                throw new GeneralException(S3ErrorCode._IO_EXCEPTION_DELETE_FILE);
            }
        }

        return new ProfileResult(newUrl);
    }

    @Transactional
    public void deleteProfileImg(ProfileCommand command){

        User user = userRepository.findById(command.userId())
                .orElseThrow(()-> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        String oldUrl = user.getProfileImageUrl();

        if (oldUrl != null && !oldUrl.isBlank()) {
            try {
                s3Service.delete(oldUrl);
            } catch (Exception e) {
                throw new GeneralException(S3ErrorCode._IO_EXCEPTION_UPLOAD_FILE);
            }
        }
        user.updateProfileImage(null);
    }

    @Transactional(readOnly = true)
    public MypageResult getMypage(UserReadCommand command){

        User user = userRepository.findById(command.userId())
                .orElseThrow(()-> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        String profileImage = user.getProfileImageUrl();

        return new MypageResult(user.getNickName(), profileImage, user.getEmail());
    }

    // 최근 본 전시 리스트 조회
    public List<ExhibitRecentResult> getRecentViews(UserReadCommand command) {

        String key = RedisUtils.getRecentViewKey(command.userId());
        Set<String> result = recommendRedisTemplate.opsForZSet().reverseRange(key, 0, 19);//시간 역순으로 가져옴

        if (result == null || result.isEmpty())
            return List.of();

        List<Long> ids= result.stream()
                .map(Long::valueOf)
                .toList();

        List<Exhibit> exhibits = exhibitRepository.findAllByIdWithHall(ids);

        exhibits.sort(Comparator.comparingInt(exhibit -> ids.indexOf(exhibit.getExhibitId())));

        return exhibits.stream()
                .map(ExhibitRecentResult::from)
                .toList();
    }

}
