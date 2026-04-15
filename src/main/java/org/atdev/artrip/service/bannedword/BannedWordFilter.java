package org.atdev.artrip.service.bannedword;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.review.BannedWord;
import org.atdev.artrip.global.apipayload.code.status.ReviewErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.BannedWordCacheService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class BannedWordFilter {

    private final BannedWordCacheService cacheService;
    private static final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

    private Pattern getPattern(String regex) {
        return patternCache.computeIfAbsent(regex, Pattern::compile);
    }

    public void validate(String content) {

        if (content == null || content.isBlank()) return;

        String normalized = normalize(content);

        List<BannedWord> bannedWords = cacheService.getActiveBannedWords();

        for (BannedWord bannedWord : bannedWords) {

            if (bannedWord.isUseRegex()) {

                Pattern pattern = getPattern(bannedWord.getWord());

                if (pattern.matcher(normalized).find()) {
                    throw new GeneralException(ReviewErrorCode._BAD_WORD_INCLUDED);
                }
            }
            else {
                String banned = normalize(bannedWord.getWord());

                if (normalized.contains(banned)) {
                    throw new GeneralException(ReviewErrorCode._BAD_WORD_INCLUDED);
                }
            }
        }
    }

    private String normalize(String content) {
        return content
                .toLowerCase()
                .replaceAll("\\s+", "")
                .replaceAll("[^가-힣a-zA-Z]", "");
    }
}