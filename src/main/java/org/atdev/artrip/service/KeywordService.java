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
import org.atdev.artrip.service.dto.command.KeywordCommand;
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
    public void saveKeywords(KeywordCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        Set<String> uniqueNames = new HashSet<>(command.keywords());
        List<Keyword> keywords = keywordRepository.findAllByNameIn(new ArrayList<>(uniqueNames));

        if (keywords.size() != command.keywords().size())
            throw new GeneralException(KeywordErrorCode._KEYWORD_NOT_FOUND);

        userKeywordRepository.deleteByUserId(command.userId());

        List<UserKeyword> userKeywords = keywords.stream()
                .map(keyword -> UserKeyword.create(user,keyword))
                .toList();

        userKeywordRepository.saveAll(userKeywords);
    }

    @Transactional(readOnly = true)
    public List<KeywordResult> getAllKeywords() {

        return keywordRepository.findAll()
                .stream()
                .map(KeywordResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<KeywordResult> getKeyword(Long userId) {
        return userKeywordRepository.findAllByUserIdWithKeyword(userId)
                .stream()
                .map(KeywordResult::from)
                .toList();
    }
}
