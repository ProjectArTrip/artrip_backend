package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.service.dto.command.AdminExhibitSearchCommand;
import org.atdev.artrip.service.dto.condition.ExhibitSearchCondition;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.result.AdminExhibitListItemResult;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExhibitRepositoryCustom {

    Slice<Exhibit> findExhibitByFilters(ExhibitSearchCondition command);

    List<ExhibitRandomResult> findRandomExhibits(ExhibitRandomCommand condition);

    long countBySearchCondition(ExhibitSearchCondition command);

    Page<AdminExhibitListItemResult> searchForAdmin(AdminExhibitSearchCommand command, Pageable pageable);
}
