package org.atdev.artrip.domain.keyword.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.atdev.artrip.domain.keyword.repository.KeywordRepository;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.atdev.artrip.domain.keyword.web.dto.KeywordResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserRepository userRepository;

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

    public List<KeywordResponse> getAllKeywords() {
        return keywordRepository.findAll()
                .stream()
                .map(k -> new KeywordResponse(k.getKeywordId(), k.getName(), k.getType()))
                .collect(Collectors.toList());
    }

    public List<KeywordResponse> getUserKeywords(Long userId) {
        return userKeywordRepository.findAllByUserUserId(userId) // UserKeyword 테이블에서 조회
                .stream()
                .map(uk -> new KeywordResponse(
                        uk.getKeyword().getKeywordId(),
                        uk.getKeyword().getName(),
                        uk.getKeyword().getType()
                ))
                .collect(Collectors.toList());
    }
}
