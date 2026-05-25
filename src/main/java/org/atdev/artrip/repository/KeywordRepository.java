package org.atdev.artrip.repository;

import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.domain.keyword.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findByNameIn(Set<String> matchedKeyword);

    List<Keyword> findAllByNameIn(List<String> keywordName);

    @Query("""
            select k 
            from Keyword k
            where (k.type = :genreType and k.name in :genres)
                or (k.type = :styleType and k.name in :styles)
            """)
    List<Keyword> findGenresAndStyles(@Param("genreType")KeywordType genreType, @Param("genres") Set<String> genres, @Param("styleType") KeywordType styleType, @Param("styles") Set<String> styles);


}