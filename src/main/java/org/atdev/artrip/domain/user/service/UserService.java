package org.atdev.artrip.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.atdev.artrip.domain.keyword.repository.KeywordRepository;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.atdev.artrip.domain.user.web.dto.request.NicknameRequest;
import org.atdev.artrip.domain.user.web.dto.response.MypageResponse;
import org.atdev.artrip.domain.user.web.dto.response.NicknameResponse;
import org.atdev.artrip.global.apipayload.code.status.S3Error;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.global.s3.web.dto.request.ImageResizeRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]+$";

    @Transactional
    public NicknameResponse updateNickName(Long userId, NicknameRequest dto){

        //1. 유저 검사
        //2. 닉네임 유효성 검사 + 공백 금지
        //3. 기존과 동일한지 체크
        //4. 중복 검사
        //5. 업뎃 후 반환
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));

        String newNick = validateNickname(dto);

        if (newNick.equals(user.getNickName())) {
            return new NicknameResponse(newNick);
        }

        if (userRepository.existsByNickName(newNick)) {
            throw new GeneralException(UserError._DUPLICATE_NICKNAME);
        }

        user.updateNickname(newNick);

        return new NicknameResponse(newNick);
    }

    private String validateNickname(NicknameRequest dto) {

        if (dto == null || dto.getNickName() == null) {
            throw new GeneralException(UserError._NICKNAME_BAD_REQUEST);
        }

        String nickname = dto.getNickName();

        if (nickname.isBlank() || nickname.contains(" ")) {
            throw new GeneralException(UserError._NICKNAME_BAD_REQUEST);
        }

        if (!nickname.matches(NICKNAME_REGEX)) {
            throw new GeneralException(UserError._NICKNAME_BAD_REQUEST);
        }

        return nickname;
    }

    @Transactional
    public String updateProfileImg(Long userId, MultipartFile image){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException(UserError._USER_NOT_FOUND));

        if (image == null || image.isEmpty()) {
            throw new GeneralException(UserError._PROFILE_IMAGE_NOT_EXIST);
        }

        String oldUrl = user.getProfileImageUrl();

        String newUrl;
        try {
            newUrl = s3Service.uploadProfile(image);
        } catch (Exception e) {
            throw new GeneralException(S3Error._IO_EXCEPTION_UPLOAD_FILE);
        }
        user.updateProfileImage(newUrl);

        if (oldUrl != null && !oldUrl.isBlank()) {
            try {
                s3Service.delete(oldUrl);
            } catch (Exception e) {
                throw new GeneralException(S3Error._IO_EXCEPTION_DELETE_FILE);
            }
        }
        return newUrl;
    }

    @Transactional
    public void deleteProfileImg(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException(UserError._USER_NOT_FOUND));

        String oldUrl = user.getProfileImageUrl();

        if (oldUrl != null && !oldUrl.isBlank()) {
            try {
                s3Service.delete(oldUrl);
            } catch (Exception e) {
                throw new GeneralException(S3Error._IO_EXCEPTION_UPLOAD_FILE);
            }
        }
        user.updateProfileImage(null);
    }

    @Transactional(readOnly = true)
    public MypageResponse getMypage(Long userId, ImageResizeRequest resize){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException(UserError._USER_NOT_FOUND));

        String profileImage = s3Service.buildResizeUrl(user.getProfileImageUrl(), resize.getW(), resize.getH(), resize.getF());

        return new MypageResponse(user.getNickName(), profileImage);
    }


}
