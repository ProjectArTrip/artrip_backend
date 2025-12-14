package org.atdev.artrip.domain.exhibit.repository;

import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.web.dto.ExhibitFilterDto;
import org.atdev.artrip.domain.exhibit.web.dto.RandomExhibitFilter;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExhibitRepositoryCustom {

    Slice<Exhibit> findExhibitByFilters(ExhibitFilterDto filter, Pageable pageable, Long cursorId);

    List<HomeListResponse> findRandomExhibits(RandomExhibitFilter condition);

}
