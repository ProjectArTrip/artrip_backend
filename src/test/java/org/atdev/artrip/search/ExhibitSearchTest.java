package org.atdev.artrip.search;

import org.atdev.artrip.search.repository.ExhibitSearchRepository;
import org.atdev.artrip.search.service.ExhibitIndexService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExhibitSearchTest {

    @Autowired
    ExhibitIndexService indexService;

    @Autowired
    ExhibitSearchRepository repository;

    @Test
    void testSearch(){
        indexService.indexAllExhibits();

        var results = repository.findByTitleContainingIgnoreCase("PAINTING");
        results.forEach(r -> System.out.println(r.getGenre()));
    }
}
