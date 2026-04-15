package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.review.BannedWord;
import org.atdev.artrip.repository.BannedWordRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannedWordCacheService {

    private final BannedWordRepository bannedWordRepository;

    @Cacheable(value = "bannedWords")
    public List<BannedWord> getActiveBannedWords() {
        return bannedWordRepository.findAllByActiveTrue();
    }

    @CacheEvict(value = "bannedWords", allEntries = true)
    public void evict() {
    }
}
