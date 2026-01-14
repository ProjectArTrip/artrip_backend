package org.atdev.artrip.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.controller.dto.response.ExhibitRecentResponse;
import org.atdev.artrip.converter.HomeConverter;
import org.atdev.artrip.controller.dto.request.NicknameRequest;
import org.atdev.artrip.controller.dto.response.MypageResponse;
import org.atdev.artrip.controller.dto.response.NicknameResponse;
import org.atdev.artrip.global.apipayload.code.status.S3ErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
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
    private final HomeConverter homeConverter;

    private static final String KEY_PREFIX = "recent:view:user:";

    @Transactional
    public NicknameResponse updateNickName(Long userId, NicknameRequest dto){

        //1. 유저 검사
        //2. 닉네임 유효성 검사 + 공백 금지
        //3. 기존과 동일한지 체크
        //4. 중복 검사
        //5. 업뎃 후 반환
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        String newNick = validateNickname(dto);

        if (newNick.equals(user.getNickName())) {
            return new NicknameResponse(newNick);
        }

        if (userRepository.existsByNickName(newNick)) {
            throw new GeneralException(UserErrorCode._DUPLICATE_NICKNAME);
        }

        user.updateNickname(newNick);

        return new NicknameResponse(newNick);
    }

    private String validateNickname(NicknameRequest dto) {

        if (dto == null || dto.getNickName() == null) {
            throw new GeneralException(UserErrorCode._NICKNAME_BAD_REQUEST);
        }

        String nickname = dto.getNickName();

        if (nickname.isBlank() || nickname.contains(" ")) {
            throw new GeneralException(UserErrorCode._NICKNAME_BAD_REQUEST);
        }

        if (!nickname.matches(NICKNAME_REGEX)) {
            throw new GeneralException(UserErrorCode._NICKNAME_BAD_REQUEST);
        }

        return nickname;
    }

    @Transactional
    public String updateProfileImg(Long userId, MultipartFile image){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        if (image == null || image.isEmpty()) {
            throw new GeneralException(UserErrorCode._PROFILE_IMAGE_NOT_EXIST);
        }

        String oldUrl = user.getProfileImageUrl();

        String newUrl;
        try {
            newUrl = s3Service.uploadProfile(image);
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
        return newUrl;
    }

    @Transactional
    public void deleteProfileImg(Long userId){

        User user = userRepository.findById(userId)
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
    public MypageResponse getMypage(Long userId, ImageResizeRequest resize){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        String profileImage = s3Service.buildResizeUrl(user.getProfileImageUrl(), resize.getW(), resize.getH(), resize.getF());

        return new MypageResponse(user.getNickName(), profileImage, user.getEmail());
    }

    // 최근 본 전시 리스트 조회
    public List<ExhibitRecentResponse> getRecentViews(Long userId) {

        String key = KEY_PREFIX + userId;
        Set<String> result = recommendRedisTemplate.opsForZSet().reverseRange(key, 0, 19);//시간 역순으로 가져옴

        if (result == null || result.isEmpty())
            return List.of();

        List<Long> ids= result.stream()
                .map(Long::valueOf)
                .toList();

        List<Exhibit> exhibits = exhibitRepository.findAllByIdWithHall(ids);

        exhibits.sort(Comparator.comparingInt(exhibit -> ids.indexOf(exhibit.getExhibitId())));

        return exhibits.stream()
                .map(exhibit -> homeConverter.toExhibitRecentResponse(exhibit))
                .toList();
    }

}
