package org.atdev.artrip.domain.keyword.repository;

import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {


}