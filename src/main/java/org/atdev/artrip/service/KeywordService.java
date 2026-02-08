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
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.dto.result.KeywordListResult;
import org.atdev.artrip.service.dto.result.KeywordResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveKeywords(Long userId, List<String> keywordNames) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        Set<String> uniqueNames = new HashSet<>(keywordNames);
        List<Keyword> keywords = keywordRepository.findAllByNameIn(new ArrayList<>(uniqueNames));

        if (keywords.size() != keywordNames.size())
            throw new GeneralException(KeywordErrorCode._KEYWORD_NOT_FOUND);

        userKeywordRepository.deleteByUserId(userId);

        List<UserKeyword> userKeywords = keywords.stream()
                .map(keyword -> UserKeyword.create(user,keyword))
                .toList();

        userKeywordRepository.saveAll(userKeywords);
    }

    @Transactional(readOnly = true)
    public KeywordListResult getAllKeywords() {

        List<Keyword> keywords = keywordRepository.findAll();
        return KeywordListResult.fromKeywords(keywords);
    }

    @Transactional(readOnly = true)
    public KeywordListResult getKeyword(Long userId) {

        List<UserKeyword> userKeywords = userKeywordRepository.findAllByUserIdWithKeyword(userId);
        return KeywordListResult.fromUserKeywords(userKeywords);
    }
}
