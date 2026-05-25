package org.atdev.artrip.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.ReviewStatus;
import org.atdev.artrip.domain.auth.QUser;
import org.atdev.artrip.domain.exhibit.QExhibit;
import org.atdev.artrip.domain.review.QReview;
import org.atdev.artrip.domain.stamp.QStamp;
import org.atdev.artrip.service.dto.command.AdminReviewSearchCommand;
import org.atdev.artrip.service.dto.result.AdminReviewResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private static final Map<String, Function<QReview, ComparableExpressionBase<?>>> SORT_FIELDS = Map.of(
            "createdAt", r -> r.createdAt,
            "visitDate", r -> r.visitDate,
            "status", r -> r.status
    );

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminReviewResult> searchForAdmin(AdminReviewSearchCommand command, Pageable pageable) {
        QReview r = QReview.review;
        QUser u = QUser.user;
        QExhibit e = QExhibit.exhibit;
        QStamp s = QStamp.stamp;


        BooleanExpression stampExists = JPAExpressions
                .selectOne()
                .from(s)
                .where(s.review.eq(r))
                .exists();

        BooleanBuilder where = new BooleanBuilder()
                .and(eqStatus(command.status()))
                .and(eqUserId(command.userId()))
                .and(keywordContains(command.keyword()))
                .and(createdBetween(command.startDate(), command.endDate()))
                .and(eqStampIssued(command.stampIssued(), stampExists));

        List<AdminReviewResult> content = queryFactory
                .select(Projections.constructor(
                        AdminReviewResult.class,
                        r.reviewId,
                        r.status,
                        r.rejectionReason,
                        r.rejectedAt,
                        u.userId,
                        u.nickName,
                        e.exhibitId,
                        e.title,
                        r.content,
                        r.visitDate,
                        r.createdAt,
                        r.updatedAt,
                        stampExists
                ))
                .from(r)
                .join(r.user, u)
                .join(r.exhibit, e)
                .where(where)
                .orderBy(toOrderSpecifiers(pageable, r))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(r.count()).from(r).where(where).fetchOne();
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanExpression eqStatus(ReviewStatus status) {
        return status == null ? null : QReview.review.status.eq(status);
    }

    private BooleanExpression eqUserId(Long userId) {
        return userId == null ? null : QReview.review.user.userId.eq(userId);
    }

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return QReview.review.exhibit.title.containsIgnoreCase(keyword)
                .or(QReview.review.user.nickName.containsIgnoreCase(keyword));
    }

    private BooleanExpression createdBetween(LocalDate start, LocalDate end) {
        QReview r = QReview.review;
        if (start == null && end == null) return null;
        if (start != null && end != null) {
            return r.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX));
        }
        if (start != null) return r.createdAt.goe(start.atStartOfDay());
        return r.createdAt.loe(end.atTime(LocalTime.MAX));
    }

    private BooleanExpression eqStampIssued(Boolean stampIssued, BooleanExpression stampExists) {
        if (stampIssued == null) return null;
        return stampIssued ? stampExists : stampExists.not();
    }

    private OrderSpecifier<?>[] toOrderSpecifiers(Pageable pageable, QReview r) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{r.createdAt.desc()};
        }
        return pageable.getSort().stream()
                .map(o -> new OrderSpecifier<>(
                        o.isAscending() ? Order.ASC : Order.DESC,
                        SORT_FIELDS.getOrDefault(o.getProperty(), x -> x.createdAt).apply(r)
                ))
                .toArray(OrderSpecifier[]::new);
    }
}
