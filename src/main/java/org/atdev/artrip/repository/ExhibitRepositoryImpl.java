package org.atdev.artrip.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibit.QExhibit;
import org.atdev.artrip.controller.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.domain.exhibitHall.QExhibitHall;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.controller.dto.request.RandomExhibitRequest;
import org.atdev.artrip.domain.keyword.QKeyword;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.atdev.artrip.domain.exhibit.QExhibit.exhibit;

@Repository
@RequiredArgsConstructor
public class ExhibitRepositoryImpl implements ExhibitRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Exhibit> findExhibitByFilters(ExhibitFilterRequest dto, Long size, Long cursorId) {

        QExhibit e = exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;
        QKeyword k = QKeyword.keyword;

        Exhibit cursor = null;

        if (cursorId != null) {
            cursor = queryFactory.selectFrom(e)
                    .where(e.exhibitId.eq(cursorId))
                    .fetchOne();
        }

        List<Exhibit> content = queryFactory
                .selectDistinct(e)
                .from(e)
                .join(e.exhibitHall, h).fetchJoin()
                .leftJoin(e.keywords, k)
                .where(
                        e.status.ne(Status.FINISHED),
                        isDomesticEq(dto.getIsDomestic()),
                        dateFilter(dto.getStartDate(), dto.getEndDate(),e),
                        cursorCondition(cursor, dto.getSortType(), e),
                        countryEq(dto.getCountry()),
                        regionEq(dto.getRegion()),
                        genreIn(dto.getGenres()),
                        styleIn(dto.getStyles())
                )
                .orderBy(sortFilter(dto, e))
                .limit(size+1)
                .fetch();

        boolean hasNext = content.size() > size;

        if (hasNext)
            content.remove(size.intValue());

        return new SliceImpl<>(content, PageRequest.of(0, size.intValue()), hasNext);
    }

    @Override
    public List<HomeListResponse> findRandomExhibits(RandomExhibitRequest c) {

        QExhibit e = exhibit;
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
                        ),
                        h.name,
                        h.country,
                        h.region
                ))
                .from(e)
                .join(e.exhibitHall, h)
                .leftJoin(e.keywords, k)
                .where(
                        e.status.ne(Status.FINISHED),
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

    private BooleanExpression cursorCondition(Exhibit cursor, SortType sortType, QExhibit e) {
        if (cursor == null) return null;
        if (sortType == null) sortType = SortType.LATEST;

        return switch (sortType) {

            case POPULAR -> e.favoriteCount.lt(cursor.getFavoriteCount())
                    .or(e.favoriteCount.eq(cursor.getFavoriteCount())
                            .and(e.exhibitId.lt(cursor.getExhibitId())));

            case LATEST -> e.startDate.lt(cursor.getStartDate())
                    .or(e.startDate.eq(cursor.getStartDate())
                            .and(e.exhibitId.lt(cursor.getExhibitId())));

            default -> e.endDate.gt(cursor.getEndDate())
                    .or(e.endDate.eq(cursor.getEndDate())
                            .and(e.exhibitId.lt(cursor.getExhibitId())));
        };
    }

    private OrderSpecifier<?>[] sortFilter(ExhibitFilterRequest dto, QExhibit e) {

        if (dto.getSortType() == null) {
            return new OrderSpecifier[]{e.startDate.desc(), e.exhibitId.desc()};
        }

        switch (dto.getSortType()) {
            case POPULAR:
                return new OrderSpecifier[]{
                        e.favoriteCount.desc().nullsLast(),
                        e.exhibitId.desc()
                };

            case ENDING_SOON:
                return new OrderSpecifier[]{
                        e.endDate.asc(),
                        e.exhibitId.desc()
                };

            default:
                return new OrderSpecifier[]{e.startDate.desc(),e.exhibitId.desc()};
        }
    }


    private BooleanExpression dateFilter(LocalDate startDate, LocalDate endDate, QExhibit e) {

        BooleanExpression condition = null;

        if (startDate == null && endDate == null) return null;

        if (endDate != null) {
            condition = e.startDate.loe(endDate);
        }

        if (startDate != null) {
            BooleanExpression startCond = e.endDate.goe(startDate);
            condition = (condition == null) ? startCond : condition.and(startCond);
        }

        return condition;
    }

    private BooleanExpression isDomesticEq(Boolean isDomestic) {
        return isDomestic == null ? null : QExhibitHall.exhibitHall.isDomestic.eq(isDomestic);
    }

    private BooleanExpression countryEq(String country) {
        return country == null || country.isBlank() ? null : QExhibitHall.exhibitHall.country.eq(country);
    }

    private BooleanExpression regionEq(String region) {
        return region == null || region.isBlank() ? null : QExhibitHall.exhibitHall.region.eq(region);
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

        return exhibit.startDate.loe(date)
                .and(exhibit.endDate.goe(date));
    }

    private BooleanExpression keywordSearch(String keyword, QExhibit e, QExhibitHall h, QKeyword k) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return e.title.containsIgnoreCase(keyword)
                .or(e.description.containsIgnoreCase(keyword))
                .or(h.name.containsIgnoreCase(keyword))
                .or(k.name.containsIgnoreCase(keyword));
    }

    @Override
    public Slice<Exhibit> searchByKeyword(String keywords, Long cursor, Long size) {
        QExhibitHall exhibitHall = QExhibitHall.exhibitHall;
        QKeyword keyword = QKeyword.keyword;
        List<Exhibit> content = queryFactory
                .selectDistinct(exhibit)
                .from(exhibit)
                .leftJoin(exhibit.exhibitHall, exhibitHall).fetchJoin()
                .join(exhibit.keywords, keyword)
                .where(
                        keywords != null ? keyword.name.contains(keywords) : null,
                        cursor != null ? exhibit.exhibitId.lt(cursor) : null
                )
                .limit(size + 1)
                .orderBy(exhibit.exhibitId.desc())
                .fetch();

        boolean hasNext = false;
        if (content.size() > size) {
            content.remove(size.intValue());
            hasNext = true;
        }
        return new SliceImpl<>(content, PageRequest.ofSize(size.intValue()),hasNext);
    }

}
