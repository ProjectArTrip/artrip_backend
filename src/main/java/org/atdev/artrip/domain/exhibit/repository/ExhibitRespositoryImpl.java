package org.atdev.artrip.domain.exhibit.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.data.QExhibit;
import org.atdev.artrip.domain.exhibit.web.dto.ExhibitFilterDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitRespositoryImpl implements ExhibitRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Exhibit> findExhibitByFilters(ExhibitFilterDto filter, Pageable pageable, Long cursorId) {

        QExhibit e =QExhibit.exhibit;

        Exhibit cursor = null;
        if (cursorId != null) {
            cursor = queryFactory.selectFrom(e)
                    .where(e.exhibitId.eq(cursorId))
                    .fetchOne();
        }




        return null;
    }

}
