package org.atdev.artrip.elastic.repository;

import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.elastic.document.ExhibitDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ExhibitDocumentRepository extends ElasticsearchRepository<ExhibitDocument, Long> {

    List<ExhibitDocument> findByTitleContainingOrKeywordsNameContaining(String title, String keywordName);

    List<ExhibitDocument> findByKeywordsType(KeywordType type);

    List<ExhibitDocument> findByKeywordsNameContaining(String keywordName);

}
