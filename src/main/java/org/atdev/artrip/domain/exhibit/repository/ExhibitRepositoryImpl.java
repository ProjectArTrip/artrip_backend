package org.atdev.artrip.domain.exhibit.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.Enum.KeywordType;
import org.atdev.artrip.domain.Enum.SortType;
import org.atdev.artrip.domain.Enum.Status;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.data.QExhibit;
import org.atdev.artrip.domain.exhibit.web.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.domain.exhibitHall.data.QExhibitHall;
import org.atdev.artrip.domain.favortie.data.QFavoriteExhibit;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.web.dto.request.RandomExhibitRequest;
import org.atdev.artrip.domain.keyword.data.QKeyword;
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
    public Slice<Exhibit> findExhibitByFilters(ExhibitFilterRequest dto, Long size, Long cursorId) {

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
                        e.status.ne(Status.FINISHED),
                        isDomesticEq(dto.getIsDomestic()),
                        dateFilter(dto.getStartDate(), dto.getEndDate(),e),
                        cursorCondition(cursor, cursorFavoriteCount, dto.getSortType(), e, f),
                        countryEq(dto.getCountry()),
                        regionEq(dto.getRegion()),
                        genreIn(dto.getGenres()),
                        styleIn(dto.getStyles())
                )
                .orderBy(sortFilter(dto, e, f))
                .limit(size+1)
                .groupBy(e.exhibitId)
                .fetch();


        boolean hasNext = content.size() > size;

        if (hasNext)
            content.remove(size.intValue());

        return new SliceImpl<>(content, PageRequest.of(0, size.intValue()), hasNext);
    }// 페이지 개념은 사용 x

    @Override
    public List<HomeListResponse> findRandomExhibits(RandomExhibitRequest c) {

        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;
        QKeyword k = QKeyword.keyword;

        return queryFactory
                .selectDistinct(Projections.constructor(// select 순서와 DTO 생성자 파라미터 순서를 1:1 매핑함!
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

    private BooleanExpression cursorCondition(Exhibit cursor,  long cursorFavoriteCount, SortType sortType, QExhibit e, QFavoriteExhibit f) {
        if (cursor == null) return null;
        if (sortType == null) sortType = SortType.LATEST;

        return switch (sortType) {

            case POPULAR -> f.favoriteId.count().loe(cursorFavoriteCount)//<=
                    .or(f.favoriteId.count().eq(cursorFavoriteCount)
                            .and(e.exhibitId.lt(cursor.getExhibitId())));//<

            case LATEST -> e.startDate.lt(cursor.getStartDate())//<
                    .or(e.startDate.eq(cursor.getStartDate())
                            .and(e.exhibitId.lt(cursor.getExhibitId())));

            default -> e.endDate.gt(cursor.getEndDate())//>
                    .or(e.endDate.eq(cursor.getEndDate())
                            .and(e.exhibitId.lt(cursor.getExhibitId())));
        };
    }

    private OrderSpecifier<?>[] sortFilter(ExhibitFilterRequest dto, QExhibit e, QFavoriteExhibit f) {

        if (dto.getSortType() == null) {
            return new OrderSpecifier[]{e.startDate.desc(), e.exhibitId.desc()};
        }

        switch (dto.getSortType()) {
            case POPULAR:
                return new OrderSpecifier[]{
                        f.favoriteId.count().desc().nullsLast(),
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

        return QExhibit.exhibit.startDate.loe(date)//<=
                .and(QExhibit.exhibit.endDate.goe(date));//>=
    }

}
