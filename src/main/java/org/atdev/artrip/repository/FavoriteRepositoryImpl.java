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

import java.time.LocalDate;
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

        LocalDate cursorCreatedAt = null;
        LocalDate cursorEndDate = null;

        if (cursor != null) {
            if (c.sortType() == SortType.ENDING_SOON) {
                cursorEndDate = queryFactory
                        .select(e.endDate)
                        .from(f)
                        .join(f.exhibit, e)
                        .where(f.favoriteId.eq(cursor))
                        .fetchOne();
                if (cursorEndDate == null) cursor = null;
            } else {
                cursorCreatedAt = queryFactory
                        .select(f.createdAt)
                        .from(f)
                        .where(f.favoriteId.eq(cursor))
                        .fetchOne();
                if (cursorCreatedAt == null) cursor = null;
            }
        }

        List<Favorite> content = queryFactory
                .selectFrom(f)
                .join(f.exhibit, e).fetchJoin()
                .join(e.exhibitHall, h).fetchJoin()
                .where(
                        f.user.userId.eq(userId),
                        f.status.eq(true),
                        e.status.ne(Status.FINISHED),
                        locationFilter(c.region(), c.country(), h),
                        cursorCondition(cursor, cursorCreatedAt, cursorEndDate, c.sortType(), f, e)
                )
                .orderBy(sortOrder(c.sortType(), f, e))
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
                        locationFilter(c.region(), c.country(), h)
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

    private BooleanExpression locationFilter(String region, String country, QExhibitHall h) {

        boolean hasRegion = region != null && !region.isBlank();
        boolean hasCountry = country != null && !country.isBlank();

        if (!hasRegion && !hasCountry) return null;

        BooleanExpression domestic = null;
        BooleanExpression overseas = null;

        if (hasRegion) {
            domestic = "전체".equals(region)
                    ? h.isDomestic.isTrue()
                    : h.isDomestic.isTrue().and(h.region.eq(region));
        }

        if (hasCountry) {
            overseas = "전체".equals(country)
                    ? h.isDomestic.isFalse()
                    : h.isDomestic.isFalse().and(h.country.eq(country));
        }

        if (domestic != null && overseas != null) return domestic.or(overseas);
        if (domestic != null) return domestic;
        return overseas;


    }

    private BooleanExpression cursorCondition(Long cursor, LocalDate cursorCreatedAt, LocalDate cursorEndDate, SortType sortType, QFavorite f, QExhibit e) {
        if (cursor == null) return null;

        if (sortType == SortType.ENDING_SOON) {
            return endingSoonCursorCondition(cursor, cursorEndDate, f, e);
        }

        return latestCursorCondition(cursor, cursorCreatedAt, f);
    }

    private BooleanExpression latestCursorCondition(Long cursor, LocalDate cursorCreatedAt, QFavorite f) {
        return f.createdAt.lt(cursorCreatedAt)
                .or(f.createdAt.eq(cursorCreatedAt).and(f.favoriteId.lt(cursor)));

    }

    private BooleanExpression endingSoonCursorCondition(Long cursor, LocalDate cursorEndDate, QFavorite f, QExhibit e) {
        return e.endDate.gt(cursorEndDate).or(e.endDate.eq(cursorEndDate).and(f.favoriteId.lt(cursor)));
    }

    private OrderSpecifier<?>[] sortOrder(SortType sortType, QFavorite f, QExhibit e) {
        if (sortType == SortType.ENDING_SOON) {
            return new OrderSpecifier[]{e.endDate.asc(), f.favoriteId.desc()};
        }
        return new OrderSpecifier[]{f.createdAt.desc(), f.favoriteId.desc()};
    }
}
