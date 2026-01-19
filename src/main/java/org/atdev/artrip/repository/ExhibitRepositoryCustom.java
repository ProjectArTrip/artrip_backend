package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.controller.dto.request.RandomExhibitRequest;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExhibitRepositoryCustom {

    Slice<Exhibit> findExhibitByFilters(ExhibitFilterRequest filter, Long size, Long cursorId);

    List<ExhibitRandomResult> findRandomExhibits(ExhibitRandomCommand condition);

}
