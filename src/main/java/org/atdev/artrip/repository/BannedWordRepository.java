package org.atdev.artrip.repository;

import org.atdev.artrip.domain.review.BannedWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannedWordRepository extends JpaRepository<BannedWord, Long> {

    List<BannedWord> findAllByActiveTrue();
}
