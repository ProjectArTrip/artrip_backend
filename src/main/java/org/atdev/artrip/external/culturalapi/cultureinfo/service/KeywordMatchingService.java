package org.atdev.artrip.external.culturalapi.cultureinfo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.repository.KeywordRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordMatchingService {

    private final KeywordRepository keywordRepository;

    private static final Map<String, List<String>> KEYWORD_PATTERNS = Map.ofEntries(
            // GENRE
            Map.entry("사진", List.of("사진", "사진전", "포토", "photo", "photography")),
            Map.entry("회화", List.of("회화", "그림", "페인팅", "painting", "드로잉")),
            Map.entry("조각", List.of("조각", "sculpture", "입체")),
            Map.entry("디지털/미디어", List.of("디지털", "미디어", "digital", "media", "NFT", "AI")),
            Map.entry("설치 미술", List.of("설치", "installation")),
            Map.entry("현대 미술", List.of("현대", "컨템포러리", "contemporary")),
            Map.entry("팝아트", List.of("팝", "팝아트", "pop art")),

            // STYLE
            Map.entry("몰입형", List.of("몰입", "immersive")),
            Map.entry("인터렉티브", List.of("체험", "인터랙티브", "interactive")),
            Map.entry("VR/AR", List.of("VR", "AR", "가상현실", "증강현실")),
            Map.entry("미디어 아트", List.of("미디어아트", "미디어 아트", "media art"))
    );

    public List<Keyword> matchKeywords(String searchText, String realmName) {

        log.info("title: {}, description : {}, realmName : {}", searchText);

        String lowerText = searchText != null ? searchText.toLowerCase() : "";
        Set<String> matchedKeywords = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : KEYWORD_PATTERNS.entrySet()) {
            for (String pattern : entry.getValue()) {
                if (lowerText.contains(pattern.toLowerCase())) {
                    matchedKeywords.add(entry.getKey());
                    break;
                }
            }
        }

        if (matchedKeywords.isEmpty()) {
            if ("전시".equals(realmName)) {
                matchedKeywords.add("현대 미술");
            } else {
                matchedKeywords.add("순수 미술");
            }
        }

        return keywordRepository.findByNameIn(matchedKeywords);
    }

}
