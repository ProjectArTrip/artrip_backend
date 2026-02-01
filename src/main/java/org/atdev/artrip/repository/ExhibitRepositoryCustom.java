package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.service.dto.command.ExhibitSearchCondition;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExhibitRepositoryCustom {

    Slice<Exhibit> findExhibitByFilters(ExhibitSearchCondition command);

    List<ExhibitRandomResult> findRandomExhibits(ExhibitRandomCommand condition);

//    Slice<Exhibit> searchByKeyword(String title, Long cursor, Long size);
}
