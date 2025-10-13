package org.atdev.artrip.elastic.repository;

import org.atdev.artrip.elastic.document.ExhibitDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ExhibitDocumentRepository extends ElasticsearchRepository<ExhibitDocument, Long> {
    List<ExhibitDocument> findByTitleContainingIgnoreCase(String keyword);

    List<ExhibitDocument> findByTitleContaining(String title);
}
