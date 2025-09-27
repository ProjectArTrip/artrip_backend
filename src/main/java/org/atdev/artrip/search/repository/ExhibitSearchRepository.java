package org.atdev.artrip.search.repository;

import org.atdev.artrip.search.document.ExhibitDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ExhibitSearchRepository extends ElasticsearchRepository<ExhibitDocument, Long> {
    List<ExhibitDocument> findByTitleContainingIgnoreCase(String keyword);

    List<ExhibitDocument> findByTitleContaining(String title);
}
