package org.atdev.artrip.elastic.repository;

import org.atdev.artrip.elastic.document.ElasticDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticExhibitSearchRepository extends ElasticsearchRepository<ElasticDocument, Long> {
    List<ElasticDocument> findByTitleContainingIgnoreCase(String keyword);

    List<ElasticDocument> findByTitleContaining(String title);
}
