package org.atdev.artrip.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.atdev.artrip.domain.keyword.repository.KeywordRepository;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserRepository userRepository;

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


}
