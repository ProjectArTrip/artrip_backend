package org.atdev.artrip.repository;

import org.atdev.artrip.domain.keyword.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findByNameIn(Set<String> matchedKeyword);

    List<Keyword> findAllByNameIn(List<String> keywordName);


}