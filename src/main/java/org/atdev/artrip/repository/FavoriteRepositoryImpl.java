package org.atdev.artrip.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.SortType;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.QExhibit;
import org.atdev.artrip.domain.exhibitHall.QExhibitHall;
import org.atdev.artrip.domain.favorite.Favorite;
import org.atdev.artrip.domain.favorite.QFavorite;
import org.atdev.artrip.service.dto.condition.FavoriteSearchCondition;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Favorite> findFavorites(Long userId, FavoriteSearchCondition c, CursorPagination cp) {

        Long cursor = cp.cursor();

        QFavorite f = QFavorite.favorite;
        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;

        List<Favorite> content = queryFactory
                .selectFrom(f)
                .join(f.exhibit, e).fetchJoin()
                .join(e.exhibitHall, h).fetchJoin()
                .where(
                        f.user.userId.eq(userId),
                        f.status.eq(true),
                        e.status.ne(Status.FINISHED),
                        locationFilter(normalize(c.regions()), normalize(c.countries()), h),
                        cursorCondition(cursor, c.sortOption(), f, e)
                )
                .orderBy(sortOrder(c.sortOption(), f, e))
                .limit(cp.size() + 1)
                .fetch();

        boolean hasNext = content.size() > cp.size();
        if (hasNext) content.remove(cp.size().intValue());

        return new SliceImpl<>(content, PageRequest.of(0, cp.size().intValue()), hasNext);

    }

    @Override
    public long countFavorites(Long userId, FavoriteSearchCondition c) {
        QFavorite f = QFavorite.favorite;
        QExhibit e = QExhibit.exhibit;
        QExhibitHall h = QExhibitHall.exhibitHall;

        Long count = queryFactory
                .select(f.count())
                .from(f)
                .join(f.exhibit, e)
                .join(e.exhibitHall, h)
                .where(
                        f.user.userId.eq(userId),
                        f.status.eq(true),
                        e.status.ne(Status.FINISHED),
                        locationFilter(normalize(c.regions()), normalize(c.countries()), h)
                ).fetchOne();
        return count == null ? 0L : count;
    }

    @Override
    public boolean existsActive(Long userId, Long exhibitId) {
        QFavorite f = QFavorite.favorite;

        Integer fetchOne = queryFactory
                .selectOne()
                .from(f)
                .where(
                        f.user.userId.eq(userId),
                        f.exhibit.exhibitId.eq(exhibitId),
                        f.status.isTrue()
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Set<Long> findActiveExhibitIds(Long userId) {
        QFavorite f = QFavorite.favorite;

        List<Long> ids = queryFactory
                .select(f.exhibit.exhibitId)
                .from(f)
                .where(
                        f.user.userId.eq(userId),
                        f.status.isTrue()
                )
                .fetch();

        return new HashSet<>(ids);
    }

    private BooleanExpression locationFilter(List<String> regions, List<String> countries, QExhibitHall h) {

        boolean includeDomestic = regions != null;
        boolean includeDomesticAll = includeDomestic && regions.isEmpty();
        boolean includeDomesticSpecific = includeDomestic && !regions.isEmpty();

        boolean includeOverseas = countries != null;
        boolean includeOverseasAll = includeOverseas && countries.isEmpty();
        boolean includeOverseasSpecific = includeOverseas && !countries.isEmpty();

        if (!includeDomestic && !includeOverseas) return null;

        BooleanExpression domesticExpr = null;
        BooleanExpression overseasExpr = null;

        if (includeDomesticAll) {
            domesticExpr = h.isDomestic.isTrue();
        } else if (includeDomesticSpecific) {
            domesticExpr = h.isDomestic.isTrue().and(h.region.in(regions));
        }

        if (includeOverseasAll) {
            overseasExpr = h.isDomestic.isFalse();
        } else if (includeOverseasSpecific) {
            overseasExpr = h.isDomestic.isFalse().and(h.country.in(countries));
        }

        if (domesticExpr != null && overseasExpr != null) return domesticExpr.or(overseasExpr);
        if (domesticExpr != null) return domesticExpr;
        return overseasExpr;
    }

    private BooleanExpression cursorCondition(Long cursor, SortType sortType, QFavorite f, QExhibit e) {
        if (cursor == null) return null;

        if (sortType == SortType.ENDING_SOON) {
            return endingSoonCursorCondition(cursor, f, e);
        }

        return latestCursorCondition(cursor, f);
    }

    private BooleanExpression latestCursorCondition(Long cursor, QFavorite f) {
        QFavorite subFavorite = new QFavorite("subFavorite");

        return f.createdAt.lt(
                JPAExpressions
                        .select(subFavorite.createdAt)
                        .from(subFavorite)
                        .where(subFavorite.favoriteId.eq(cursor))
        ).or(
                f.createdAt.eq(
                        JPAExpressions
                                .select(subFavorite.createdAt)
                                .from(subFavorite)
                                .where(subFavorite.favoriteId.eq(cursor))
                ).and(f.favoriteId.lt(cursor))
        );
    }

    private BooleanExpression endingSoonCursorCondition(Long cursor, QFavorite f, QExhibit e) {
        QFavorite subFavorite = new QFavorite("subFavorite");
        QExhibit subExhibit = new QExhibit("subExhibit");

        return e.endDate.gt(
                JPAExpressions
                        .select(subExhibit.endDate)
                        .from(subFavorite)
                        .join(subFavorite.exhibit, subExhibit)
                        .where(subFavorite.favoriteId.eq(cursor))
        ).or(
                e.endDate.eq(
                        JPAExpressions
                                .select(subExhibit.endDate)
                                .from(subFavorite)
                                .join(subFavorite.exhibit, subExhibit)
                                .where(subFavorite.favoriteId.eq(cursor))

                ).and(f.favoriteId.lt(cursor))
        );
    }

    private OrderSpecifier<?>[] sortOrder(SortType sortType, QFavorite f, QExhibit e) {
        if (sortType == SortType.ENDING_SOON) {
            return new OrderSpecifier[]{e.endDate.asc(), f.favoriteId.desc()};
        }
        return new OrderSpecifier[]{f.createdAt.desc(), f.favoriteId.desc()};
    }

    private List<String> normalize(List<String> list) {
        if (list == null) return null;

        List<String> filtered = list.stream()
                .filter(s -> s != null && !s.equals("전체"))
                .toList();

        return filtered;
    }

}
