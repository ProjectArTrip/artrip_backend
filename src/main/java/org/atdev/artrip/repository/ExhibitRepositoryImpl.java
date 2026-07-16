package org.atdev.artrip.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibit.QExhibit;
import org.atdev.artrip.domain.exhibitHall.QExhibitHall;
import org.atdev.artrip.domain.keyword.QKeyword;
import org.atdev.artrip.service.dto.command.AdminExhibitSearchCommand;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;
import org.atdev.artrip.service.dto.condition.ExhibitSearchCondition;
import org.atdev.artrip.service.dto.result.AdminExhibitListResult;
import org.atdev.artrip.service.dto.result.ExhibitRandomResult;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ExhibitRepositoryImpl implements ExhibitRepositoryCustom {

    private static final Map<String, Function<QExhibit, ComparableExpressionBase<?>>> ADMIN_SORT_FIELDS = Map.of(
            "createdAt", e -> e.createdAt,
            "startDate", e -> e.startDate,
            "endDate", e -> e.endDate,
            "title", e -> e.title
    );

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Exhibit> findExhibitByFilters(ExhibitSearchCondition c) {

        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;

        Exhibit cursor = null;

        if (c.cursor() != null) {
            cursor = queryFactory.selectFrom(e)
                    .where(e.exhibitId.eq(c.cursor()))
                    .fetchOne();
        }

        List<Exhibit> content = queryFactory
                .selectFrom(e)
                .join(e.exhibitHall, h).fetchJoin()
                .where(
                        e.status.ne(Status.FINISHED),
                        isDomesticEq(h, c.isDomestic()),
                        dateFilter(c.startDate(), c.endDate(), e),
                        cursorCondition(cursor, c.sortType(), e),
                        countryEq(h, c.country()),
                        regionEq(h, c.region()),
                        keywordExists(e, KeywordType.GENRE, c.genres()),
                        keywordExists(e, KeywordType.STYLE, c.styles()),
                        queryContain(e, h, c.query())
                )
                .orderBy(sortFilter(c, e))
                .limit(c.size() + 1)
                .fetch();

        boolean hasNext = content.size() > c.size();

        if (hasNext)
            content.remove(c.size().intValue());

        return new SliceImpl<>(content, PageRequest.of(0, c.size().intValue()), hasNext);
    }

    @Override
    public long countBySearchCondition(ExhibitSearchCondition c) {
        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;

        Long count = queryFactory
                .select(e.count())
                .from(e)
                .join(e.exhibitHall, h)
                .where(
                        e.status.ne(Status.FINISHED),
                        isDomesticEq(h, c.isDomestic()),
                        dateFilter(c.startDate(), c.endDate(), e),
                        countryEq(h, c.country()),
                        regionEq(h, c.region()),
                        keywordExists(e, KeywordType.GENRE, c.genres()),
                        keywordExists(e, KeywordType.STYLE, c.styles()),
                        queryContain(e, h, c.query())
                )
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public List<ExhibitRandomResult> findRandomExhibits(ExhibitRandomCommand c) {
        double pivot = Math.random();
        long limit = c.limit();

        List<ExhibitRandomResult> first = findRandomExhibitsByPivot(c, pivot, true, limit);

        if (first.size() == limit) {
            return first;
        }

        long remain = limit - first.size();
        List<ExhibitRandomResult> second = findRandomExhibitsByPivot(c, pivot, false, remain);

        first.addAll(second);
        return first;
    }

    private List<ExhibitRandomResult> findRandomExhibitsByPivot(
            ExhibitRandomCommand c,
            double pivot,
            boolean greaterOrEqual,
            long limit
    ) {
        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;

        BooleanExpression randomCondition = greaterOrEqual
                ? e.randomKey.goe(pivot)
                : e.randomKey.lt(pivot);

        List<Exhibit> exhibits = queryFactory
                .selectFrom(e)
                .join(e.exhibitHall, h).fetchJoin()
                .where(
                        e.status.ne(Status.FINISHED),
                        isDomesticEq(h, c.isDomestic()),
                        countryEq(h, c.country()),
                        regionEq(h, c.region()),
                        keywordExists(e, KeywordType.GENRE, c.genres()),
                        keywordExists(e, KeywordType.STYLE, c.styles()),
                        findDate(e, c.date()),
                        randomCondition
                )
                .orderBy(e.randomKey.asc())
                .limit(limit)
                .fetch();

        return exhibits.stream()
                .map(exhibit -> ExhibitRandomResult.of(exhibit, false, ""))
                .collect(Collectors.toCollection(ArrayList::new));

    }

    @Override
    public Page<AdminExhibitListResult> searchForAdmin(AdminExhibitSearchCommand command, Pageable pageable) {
        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;

        BooleanBuilder where = new BooleanBuilder()
                .and(eqStatus(e, command.status()))
                .and(eqCountry(h, command.country()))
                .and(eqRegion(h, command.region()))
                .and(genreContains(e, command.genre()))
                .and(startDateGoe(e, command.startDate()))
                .and(endDateLoe(e, command.endDate()))
                .and(queryContain(e, h, command.keyword()));

        List<Tuple> rows = queryFactory
                .select(e.exhibitId,
                        e.title,
                        e.startDate,
                        e.endDate,
                        h.country,
                        h.region,
                        e.status,
                        h.name)
                .from(e)
                .join(e.exhibitHall, h)
                .where(where)
                .orderBy(adminOrderSpecifiers(pageable, e))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> exhibitIds = rows.stream().map(r -> r.get(e.exhibitId)).toList();
        Map<Long, List<String>> genresByExhibit = fetchGenresByExhibit(exhibitIds);

        List<AdminExhibitListResult> content = rows.stream()
                .map(r -> new AdminExhibitListResult(
                        r.get(e.exhibitId),
                        r.get(e.title),
                        r.get(e.startDate),
                        r.get(e.endDate),
                        r.get(h.country),
                        r.get(h.region),
                        r.get(e.status),
                        r.get(h.name),
                        genresByExhibit.getOrDefault(r.get(e.exhibitId), List.of())
                ))
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(e.count())
                .from(e)
                .join(e.exhibitHall, h)
                .where(where);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private Map<Long, List<String>> fetchGenresByExhibit(List<Long> exhibitIds) {
        if (exhibitIds.isEmpty()) return Map.of();

        QExhibit e = QExhibit.exhibit;
        QKeyword k = QKeyword.keyword;

        List<Tuple> tuples = queryFactory
                .select(e.exhibitId, k.name)
                .from(e)
                .join(e.keywords, k)
                .where(e.exhibitId.in(exhibitIds).and(k.type.eq(KeywordType.GENRE)))
                .orderBy(e.exhibitId.asc(), k.name.desc())
                .fetch();

        return tuples.stream().collect(Collectors.groupingBy(
                t -> t.get(e.exhibitId),
                Collectors.mapping(t -> t.get(k.name), Collectors.toList())
        ));
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

    private OrderSpecifier<?>[] sortFilter(ExhibitSearchCondition dto, QExhibit e) {

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
                return new OrderSpecifier[]{e.startDate.desc(), e.exhibitId.desc()};
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

    private BooleanExpression isDomesticEq(QExhibitHall h, Boolean isDomestic) {
        return isDomestic == null ? null : h.isDomestic.eq(isDomestic);
    }

    private BooleanExpression countryEq(QExhibitHall h, String country) {
        return country == null ? null : h.country.eq(country);
    }

    private BooleanExpression regionEq(QExhibitHall h, String region) {
        return region == null ? null : h.region.eq(region);
    }

    private BooleanExpression keywordExists(QExhibit exhibit, KeywordType type, Set<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return null;

        QKeyword keyword = new QKeyword(type.name().toLowerCase() + "Keyword");

        BooleanExpression nameCondition = null;
        for (String kw : keywords) {
            BooleanExpression like = keyword.name.containsIgnoreCase(kw);
            nameCondition = (nameCondition == null) ? like : nameCondition.or(like);
        }

        return JPAExpressions
                .selectOne()
                .from(keyword)
                .where(
                        keyword.type.eq(type),
                        nameCondition,
                        exhibit.keywords.contains(keyword)
                )
                .exists();
    }


    private BooleanExpression findDate(QExhibit e, LocalDate date) {
        if (date == null) return null;

        return e.startDate.loe(date).and(e.endDate.goe(date));
    }

    private BooleanExpression queryContain(QExhibit e, QExhibitHall h, String query) {
        if (query == null || query.isBlank()) {
            return null;
        }

        String trimmed = query.trim();
        QKeyword k = new QKeyword("searchKeyword");

        BooleanExpression keywordExists = JPAExpressions
                .selectOne()
                .from(k)
                .where(
                        e.keywords.contains(k),
                        k.name.containsIgnoreCase(trimmed)
                )
                .exists();

        return e.title.containsIgnoreCase(trimmed)
                .or(h.name.containsIgnoreCase(trimmed))
                .or(h.country.containsIgnoreCase(trimmed))
                .or(h.region.containsIgnoreCase(trimmed))
                .or(keywordExists);
    }

    private BooleanExpression eqStatus(QExhibit e, Status status) {
        return status == null ? null : e.status.eq(status);
    }

    private BooleanExpression genreContains(QExhibit e, String genre) {
        if (genre == null || genre.isBlank()) return null;
        QKeyword k = new QKeyword("adminGenreKeyword");
        return JPAExpressions
                .selectOne()
                .from(k)
                .where(
                        k.type.eq(KeywordType.GENRE),
                        k.name.containsIgnoreCase(genre.trim()),
                        e.keywords.contains(k)
                )
                .exists();
    }

    private BooleanExpression startDateGoe(QExhibit e, LocalDate startDate) {
        return startDate == null ? null : e.startDate.goe(startDate);
    }

    private BooleanExpression endDateLoe(QExhibit e, LocalDate endDate) {
        return endDate == null ? null : e.endDate.loe(endDate);
    }

    private BooleanExpression eqCountry(QExhibitHall h, String country) {
        return (country == null || country.isBlank()) ? null : h.country.eq(country);
    }

    private BooleanExpression eqRegion(QExhibitHall h, String region) {
        return (region == null || region.isBlank()) ? null : h.region.eq(region);
    }

    private OrderSpecifier<?>[] adminOrderSpecifiers(Pageable pageable, QExhibit e) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{e.createdAt.desc()};
        }
        return pageable.getSort().stream()
                .map(o -> new OrderSpecifier<>(
                        o.isAscending() ? Order.ASC : Order.DESC,
                        ADMIN_SORT_FIELDS.getOrDefault(o.getProperty(), x -> x.createdAt).apply(e)
                ))
                .toArray(OrderSpecifier[]::new);
    }

}
