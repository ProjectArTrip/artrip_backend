package org.atdev.artrip.repository;

import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.elastic.document.ExhibitDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ExhibitDocumentRepository extends ElasticsearchRepository<ExhibitDocument, Long> {

    List<ExhibitDocument> findByTitleContainingOrKeywordsNameContaining(String title, String keywordName);

    List<ExhibitDocument> findByKeywordsType(KeywordType type);

    List<ExhibitDocument> findByKeywordsNameContaining(String keywordName);

}
