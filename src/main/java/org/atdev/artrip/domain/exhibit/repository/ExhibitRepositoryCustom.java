package org.atdev.artrip.domain.exhibit.repository;

import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.web.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.web.dto.request.RandomExhibitRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExhibitRepositoryCustom {

    Slice<Exhibit> findExhibitByFilters(ExhibitFilterRequest filter, Long size, Long cursorId);

    List<HomeListResponse> findRandomExhibits(RandomExhibitRequest condition);

}
