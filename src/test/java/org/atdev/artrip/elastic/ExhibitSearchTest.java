package org.atdev.artrip.elastic;

import org.atdev.artrip.elastic.repository.ElasticExhibitSearchRepository;
import org.atdev.artrip.elastic.service.ElasticExhibitIndexService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExhibitSearchTest {

    @Autowired
    ElasticExhibitIndexService indexService;

    @Autowired
    ElasticExhibitSearchRepository repository;

    @Test
    void testSearch(){
        indexService.indexAllExhibits();

        var results = repository.findByTitleContainingIgnoreCase("PAINTING");
        results.forEach(r -> System.out.println(r.getGenre()));
    }
}
