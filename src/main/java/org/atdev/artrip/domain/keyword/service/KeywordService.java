package org.atdev.artrip.domain.keyword.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.keyword.repository.KeywordRepository;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.atdev.artrip.domain.keyword.web.dto.KeywordResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;

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
