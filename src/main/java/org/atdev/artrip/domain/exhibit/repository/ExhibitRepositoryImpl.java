package org.atdev.artrip.domain.exhibit.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.Enum.SortType;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.data.QExhibit;
import org.atdev.artrip.domain.exhibit.web.dto.request.ExhibitFilterRequestDto;
import org.atdev.artrip.domain.home.web.dto.RandomExhibitFilterRequestDto;
import org.atdev.artrip.domain.exhibitHall.data.QExhibitHall;
import org.atdev.artrip.domain.favortie.data.QFavoriteExhibit;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.keyword.data.QKeyword;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ExhibitRepositoryImpl implements ExhibitRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Exhibit> findExhibitByFilters(ExhibitFilterRequestDto dto, Pageable pageable, Long cursorId) {

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
                        dateFilter(dto.getStartDate(), dto.getEndDate(),e),
                        cursorCondition(cursor, cursorFavoriteCount, dto.getSortType(), e, f),
                        countryEq(dto.getCountry()),
                        regionEq(dto.getRegion()),
                        genreIn(dto.getGenres()),
                        styleIn(dto.getStyles())
                )
                .orderBy(sortFilter(dto, e, f))
                .limit(pageable.getPageSize() + 1)
                .groupBy(e.exhibitId)
                .fetch();


        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.remove(pageable.getPageSize());

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<HomeListResponse> findRandomExhibits(RandomExhibitFilterRequestDto c) {

        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;
        QKeyword k = QKeyword.keyword;

        return queryFactory
                .selectDistinct(Projections.constructor(
                        HomeListResponse.class,
                        e.exhibitId,
                        e.title,
                        e.posterUrl,
                        e.status,
                        Expressions.stringTemplate(
                                "concat({0}, ' ~ ', {1})",
                                e.startDate.stringValue(),
                                e.endDate.stringValue()
                        )
                ))
                .from(e)
                .join(e.exhibitHall, h)
                .leftJoin(e.keywords, k)
                .where(
                        isDomesticEq(c.getIsDomestic()),
                        countryEq(c.getCountry()),
                        regionEq(c.getRegion()),
                        genreIn(c.getGenres()),
                        styleIn(c.getStyles()),
                        findDate(c.getDate())
                )
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .limit(c.getLimit())
                .fetch();
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

    private OrderSpecifier<?>[] sortFilter(ExhibitFilterRequestDto dto, QExhibit e, QFavoriteExhibit f) {

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

    private BooleanExpression typeFilter(ExhibitFilterRequestDto dto, QExhibitHall h) {
        if (dto.getType() == null) return null;

        if ("DOMESTIC".equalsIgnoreCase(dto.getType())) {
            return h.isDomestic.isTrue();
        } else if ("OVERSEAS".equalsIgnoreCase(dto.getType())) {
            return h.isDomestic.isFalse();
        }
        return null;
    }

    private BooleanExpression dateFilter(LocalDate startDate, LocalDate endDate, QExhibit e) {

        BooleanExpression condition = null;

        if (startDate == null && endDate == null) return null;

        if (endDate != null) {
            condition = e.startDate.loe(endDate.atTime(23, 59, 59));
        }

        if (startDate != null) {
            BooleanExpression startCond = e.endDate.goe(startDate.atStartOfDay());
            condition = (condition == null) ? startCond : condition.and(startCond);
        }

        return condition;
    }

    private BooleanExpression isDomesticEq(Boolean isDomestic) {
        return isDomestic == null ? null : QExhibitHall.exhibitHall.isDomestic.eq(isDomestic);
    }

    private BooleanExpression countryEq(String country) {
        return country == null ? null : QExhibitHall.exhibitHall.country.eq(country);
    }

    private BooleanExpression regionEq(String region) {
        return region == null ? null : QExhibitHall.exhibitHall.region.eq(region);
    }

    private BooleanExpression genreIn(Set<String> genres) {
        if (genres == null || genres.isEmpty()) return null;
        return QKeyword.keyword.type.eq(KeywordType.GENRE)
                .and(QKeyword.keyword.name.in(genres));
    }

    private BooleanExpression styleIn(Set<String> styles) {
        if (styles == null || styles.isEmpty()) return null;
        return QKeyword.keyword.type.eq(KeywordType.STYLE)
                .and(QKeyword.keyword.name.in(styles));
    }

    private BooleanExpression findDate(LocalDate date){
        if (date == null) return null;

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(23, 59, 59);

        return QExhibit.exhibit.startDate.loe(dayEnd)//<=
                .and(QExhibit.exhibit.endDate.goe(dayStart));//>=
    }


}
