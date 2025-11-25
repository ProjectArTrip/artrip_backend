package org.atdev.artrip.domain.exhibit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.data.QExhibit;
import org.atdev.artrip.domain.exhibit.web.dto.ExhibitFilterDto;
import org.atdev.artrip.domain.exhibitHall.data.QExhibitHall;
import org.atdev.artrip.domain.keyword.data.QKeyword;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExhibitRepositoryImpl implements ExhibitRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Exhibit> findExhibitByFilters(ExhibitFilterDto dto, Pageable pageable) {

        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;
        QKeyword k = QKeyword.keyword;

        List<Exhibit> content = queryFactory
                .selectDistinct(e)
                .from(e)
                .join(e.exhibitHall, h)
                .leftJoin(e.keywords, k)
                .where(
                        typeFilter(dto, h),
                        dateFilter(dto, e),
                        countryFilter(dto, h),
                        regionFilter(dto, h),
                        genreFilter(dto, k),
                        styleFilter(dto, k)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(e.exhibitId.countDistinct())
                .from(e)
                .join(e.exhibitHall, h)
                .leftJoin(e.keywords, k)
                .where(
                        typeFilter(dto, h),
                        dateFilter(dto, e),
                        countryFilter(dto, h),
                        regionFilter(dto, h),
                        genreFilter(dto, k),
                        styleFilter(dto, k)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression typeFilter(ExhibitFilterDto dto, QExhibitHall h) {
        if (dto.getType() == null) return null;

        if ("DOMESTIC".equalsIgnoreCase(dto.getType())) {
            return h.isDomestic.isTrue();
        } else if ("OVERSEAS".equalsIgnoreCase(dto.getType())) {
            return h.isDomestic.isFalse();
        }
        return null;
    }

    private BooleanExpression dateFilter(ExhibitFilterDto dto, QExhibit e) {
        BooleanExpression condition = null;

        if (dto.getEndDate() != null) {
            condition = e.startDate.loe(dto.getEndDate().atTime(23, 59, 59));
        }

        if (dto.getStartDate() != null) {
            BooleanExpression endCond = e.endDate.goe(dto.getStartDate().atStartOfDay());
            condition = (condition == null) ? endCond : condition.and(endCond);
        }

        return condition;
    }

    private BooleanExpression countryFilter(ExhibitFilterDto dto, QExhibitHall h) {
        return dto.getCountry() != null ? h.country.eq(dto.getCountry()) : null;
    }

    private BooleanExpression regionFilter(ExhibitFilterDto dto, QExhibitHall h) {
        return dto.getRegion() != null ? h.region.eq(dto.getRegion()) : null;
    }

    private BooleanExpression genreFilter(ExhibitFilterDto dto, QKeyword k) {
        if (dto.getGenres() == null || dto.getGenres().isEmpty()) return null;
        return k.type.eq(KeywordType.GENRE)
                .and(k.name.in(dto.getGenres()));
    }

    private BooleanExpression styleFilter(ExhibitFilterDto dto, QKeyword k) {
        if (dto.getStyles() == null || dto.getStyles().isEmpty()) return null;
        return k.type.eq(KeywordType.STYLE)
                .and(k.name.in(dto.getStyles()));
    }

}
