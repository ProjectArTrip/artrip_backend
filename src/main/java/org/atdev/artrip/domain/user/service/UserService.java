package org.atdev.artrip.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.atdev.artrip.domain.keyword.repository.KeywordRepository;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.atdev.artrip.domain.user.web.dto.requestDto.NicknameRequestDto;
import org.atdev.artrip.domain.user.web.dto.responseDto.NicknameResponseDto;
import org.atdev.artrip.global.apipayload.code.status.S3Error;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public void saveUserKeywords(Long userId, List<Long> keywordIds) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));

        userKeywordRepository.deleteByUser(user);

        List<Keyword> keywords = keywordRepository.findAllById(keywordIds);

        List<UserKeyword> userKeywords = keywords.stream()
                .map(keyword -> UserKeyword.builder()
                        .user(user)
                        .keyword(keyword)
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        userKeywordRepository.saveAll(userKeywords);
    }

    @Transactional
    public NicknameResponseDto updateNickName(Long userId, NicknameRequestDto dto){

        //1. 유저 검사
        //2. 닉네임 유효성 검사 + 공백 금지
        //3. 기존과 동일한지 체크
        //4. 중복 검사
        //5. 업뎃 후 반환
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));

        String newNick = validateNickname(dto);

        if (newNick.equals(user.getNickName())) {
            return new NicknameResponseDto(newNick);
        }

        if (userRepository.existsByNickName(newNick)) {
            throw new GeneralException(UserError._DUPLICATE_NICKNAME);
        }

        user.updateNickname(newNick);

        return new NicknameResponseDto(newNick);
    }

    private String validateNickname(NicknameRequestDto dto) {

        if (dto == null || dto.getNickName() == null) {
            throw new GeneralException(UserError._NICKNAME_BAD_REQUEST);
        }

        String nickname = dto.getNickName().trim();

        if (nickname.isBlank() || nickname.contains(" ")) {
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

        String newUrl;
        try {
            newUrl = s3Service.upload(image);
        } catch (Exception e) {
            throw new GeneralException(S3Error._IO_EXCEPTION_UPLOAD_FILE);
        }
        user.updateProfileImage(newUrl);

        String oldUrl = user.getProfileImageUrl();
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
}
