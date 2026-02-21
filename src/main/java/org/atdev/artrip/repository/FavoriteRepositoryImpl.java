package org.atdev.artrip.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Favorite> findFavorites(Long userId, FavoriteSearchCondition c, CursorPagination cp) {

        Long cursor = c.sortType() == SortType.ENDING_SOON ? null : cp.getCursor();

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
                        cursorCondition(cursor, c.sortType(), f)
                )
                .orderBy(sortOrder(c.sortType(), f, e))
                .limit(cp.getSize() + 1)
                .fetch();

        boolean hasNext = content.size() > cp.getSize();
        if (hasNext) content.remove(cp.getSize().intValue());

        return new SliceImpl<>(content, PageRequest.of(0, cp.getSize().intValue()), hasNext);

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

    private BooleanExpression cursorCondition(Long cursor, SortType sortType, QFavorite f) {
        if (cursor == null) return null;
        if (sortType == SortType.ENDING_SOON) return null;

        return f.favoriteId.lt(cursor);
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
