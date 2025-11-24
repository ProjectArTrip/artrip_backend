package org.atdev.artrip.domain.exhibit.repository;

import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.web.dto.ExhibitFilterDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ExhibitRepositoryCustom {

    Slice<Exhibit> findExhibitByFilters(ExhibitFilterDto filter, Pageable pageable, Long cursorId);

}
