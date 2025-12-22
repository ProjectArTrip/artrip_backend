package org.atdev.artrip.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.atdev.artrip.domain.keyword.repository.KeywordRepository;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.atdev.artrip.domain.user.web.dto.requestDto.MypageRequestDto;
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
    public void updateNickName(Long userId, MypageRequestDto dto){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException(UserError._USER_NOT_FOUND));

        String newNick = dto.getNickName().trim();

        if (!newNick.equals(user.getNickName())) {
            if (userRepository.existsByNickname(newNick)) {
                throw new GeneralException(UserError._DUPLICATE_NICKNAME);
            }
            user.updateNickname(newNick);
        }
    }

    @Transactional
    public String updateProfileImg(Long userId, MultipartFile images){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException(UserError._USER_NOT_FOUND));

        if(images != null && !images.isEmpty()){

            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isBlank()) {
                s3Service.delete(user.getProfileImageUrl());
            }

            s3Service.upload()

            user.updateProfileImage(profileUrl);
        }

        return user.getProfileImageUrl();
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
