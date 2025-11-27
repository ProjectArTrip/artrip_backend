package org.atdev.artrip.domain.exhibit.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.Enum.SortType;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.data.QExhibit;
import org.atdev.artrip.domain.exhibit.web.dto.ExhibitFilterDto;
import org.atdev.artrip.domain.exhibitHall.data.QExhibitHall;
import org.atdev.artrip.domain.favortie.data.QFavoriteExhibit;
import org.atdev.artrip.domain.keyword.data.QKeyword;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExhibitRepositoryImpl implements ExhibitRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Exhibit> findExhibitByFilters(ExhibitFilterDto dto, Pageable pageable, Long cursorId) {

        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;
        QKeyword k = QKeyword.keyword;
        QFavoriteExhibit f = QFavoriteExhibit.favoriteExhibit;

        Exhibit cursor = null;

        long cursorFavoriteCount = 0;
        if (cursorId != null) {
            cursor = queryFactory.selectFrom(e)
                    .where(e.exhibitId.eq(cursorId))
                    .fetchOne();

            if (cursor != null && dto.getSortType() == SortType.POPULAR) {
                Long count = queryFactory
                        .select(f.favoriteId.count())
                        .from(f)
                        .where(f.exhibit.eq(cursor))
                        .fetchOne();

                cursorFavoriteCount = count != null ? count : 0L;
            }
        }

        List<Exhibit> content = queryFactory
                .select(e)
                .from(e)
                .join(e.exhibitHall, h)
                .leftJoin(e.keywords, k)
                .leftJoin(f).on(f.exhibit.eq(e))
                .where(
                        typeFilter(dto, h),
                        dateFilter(dto, e),
                        countryFilter(dto, h),
                        regionFilter(dto, h),
                        genreFilter(dto, k),
                        styleFilter(dto, k),
                        cursorCondition(cursor, cursorFavoriteCount, dto.getSortType(), e, f)
                )
                .orderBy(sortFilter(dto, e, f))
                .limit(pageable.getPageSize() + 1)
                .groupBy(e.exhibitId)
                .fetch();


        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.remove(pageable.getPageSize());

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression cursorCondition(Exhibit cursor,  long cursorFavoriteCount, SortType sortType, QExhibit e, QFavoriteExhibit f) {
        if (cursor == null) return null;

        return switch (sortType) {

            case POPULAR -> f.favoriteId.count().loe(cursorFavoriteCount)
                    .or(f.favoriteId.count().eq(cursorFavoriteCount)
                            .and(e.exhibitId.lt(cursor.getExhibitId())));

            case LATEST -> e.createdAt.lt(cursor.getCreatedAt())
                    .or(e.createdAt.eq(cursor.getCreatedAt())
                            .and(e.exhibitId.lt(cursor.getExhibitId())));

            case ENDING_SOON -> e.endDate.gt(cursor.getEndDate())
                    .or(e.endDate.eq(cursor.getEndDate())
                            .and(e.exhibitId.lt(cursor.getExhibitId())));
        };
    }

    private OrderSpecifier<?>[] sortFilter(ExhibitFilterDto dto, QExhibit e, QFavoriteExhibit f) {

        if (dto.getSortType() == null) {
            return new OrderSpecifier[]{e.createdAt.desc()};
        }

        switch (dto.getSortType()) {
            case POPULAR:
                return new OrderSpecifier[]{
                        f.favoriteId.count().desc().nullsLast(), // 인기순
                        e.createdAt.desc()                       // 동률일 때 최신순
                };

//            case LATEST:
//                return new OrderSpecifier[]{e.createdAt.desc()};
            case ENDING_SOON:
                return new OrderSpecifier[]{e.endDate.asc()};

            default:
                return new OrderSpecifier[]{e.createdAt.desc()};
        }
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
