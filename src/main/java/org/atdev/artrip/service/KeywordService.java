package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.KeywordErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.atdev.artrip.repository.KeywordRepository;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.controller.dto.response.KeywordResponse;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.dto.command.KeywordCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveUserKeywords(KeywordCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        Set<String> uniqueNames = new HashSet<>(command.keywords());
        List<Keyword> keywords = keywordRepository.findAllByNameIn(new ArrayList<>(uniqueNames));

        if (keywords.size() != command.keywords().size())
            throw new GeneralException(KeywordErrorCode._KEYWORD_NOT_FOUND);

        keywordRepository.deleteByUserId(command.userId());

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
    public List<KeywordResponse> getAllKeywords() {
        return keywordRepository.findAll()
                .stream()
                .map(k -> new KeywordResponse(k.getKeywordId(), k.getName(), k.getType()))
                .collect(Collectors.toList());
    }

    @Transactional
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
