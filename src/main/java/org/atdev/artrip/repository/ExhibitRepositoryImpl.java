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
import org.atdev.artrip.domain.exhibitHall.QExhibitHall;
import org.atdev.artrip.domain.keyword.QKeyword;
import org.atdev.artrip.service.dto.command.ExhibitFilterCommand;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
@Repository
@RequiredArgsConstructor
public class ExhibitRepositoryImpl implements ExhibitRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Exhibit> findExhibitByFilters(ExhibitFilterCommand c) {

        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;
        QKeyword k = QKeyword.keyword;

        Exhibit cursor = null;

        if (c.cursor() != null) {
            cursor = queryFactory.selectFrom(e)
                    .where(e.exhibitId.eq(c.cursor()))
                    .fetchOne();
        }

        List<Exhibit> content = queryFactory
                .selectDistinct(e)
                .from(e)
                .join(e.exhibitHall, h)
                .leftJoin(e.keywords, k)
                .where(
                        e.status.ne(Status.FINISHED),
                        isDomesticEq(c.isDomestic()),
                        dateFilter(c.startDate(), c.endDate(),e),
                        cursorCondition(cursor, c.sortType(), e),
                        countryEq(c.country()),
                        regionEq(c.region()),
                        genreIn(c.genres()),
                        styleIn(c.styles())
                )
                .orderBy(sortFilter(c, e))
                .limit(c.size()+1)
                .fetch();

        boolean hasNext = content.size() > c.size();

        if (hasNext)
            content.remove(c.size().intValue());

        return new SliceImpl<>(content, PageRequest.of(0, c.size().intValue()), hasNext);
    }

    @Override
    public List<ExhibitRandomResult> findRandomExhibits(ExhibitRandomCommand c) {

        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;
        QKeyword k = QKeyword.keyword;

        return queryFactory
                .selectDistinct(Projections.constructor(
                        ExhibitRandomResult.class,
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
                        h.region,
                        Expressions.asBoolean(false),
                        Expressions.asString("")
                ))
                .from(e)
                .join(e.exhibitHall, h)
                .join(e.keywords, k)
                .where(
                        e.status.ne(Status.FINISHED),
                        isDomesticEq(c.isDomestic()),
                        countryEq(c.country()),
                        regionEq(c.region()),
                        genreIn(c.genres()),
                        styleIn(c.styles()),
                        findDate(c.date())
                )
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .limit(c.limit())
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

    private OrderSpecifier<?>[] sortFilter(ExhibitFilterCommand dto, QExhibit e) {

        if (dto.sortType() == null) {
            return new OrderSpecifier[]{e.startDate.desc(), e.exhibitId.desc()};
        }

        switch (dto.sortType()) {
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

        return QExhibit.exhibit.startDate.loe(date)
                .and(QExhibit.exhibit.endDate.goe(date));
    }

}
