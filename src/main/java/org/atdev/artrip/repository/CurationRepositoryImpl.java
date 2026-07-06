package org.atdev.artrip.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.curation.QCuration;
import org.atdev.artrip.domain.curation.QCurationExhibit;
import org.atdev.artrip.service.dto.command.AdminCurationSearchCommand;
import org.atdev.artrip.service.dto.result.AdminCurationListResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CurationRepositoryImpl implements CurationRepositoryCustom {

    private final JPAQueryFactory  queryFactory;

    @Override
    public Page<AdminCurationListResult> searchForAdmin(AdminCurationSearchCommand command, Pageable pageable) {
        QCuration c =  QCuration.curation;
        QCurationExhibit ce =  QCurationExhibit.curationExhibit;

        BooleanBuilder where = new BooleanBuilder()
                .and(containsKeyword(c, command.keyword()))
                .and(eqActive(c, command.active()))
                .and(visibleFromGoe(c, command.startDate()))
                .and(visibleToLoe(c, command.endDate()));

        Expression<Long> exhibitCount = JPAExpressions
                .select(ce.count())
                .from(ce)
                .where(ce.curation.eq(c));

        List<Tuple> rows = queryFactory
                .select(c.curationId, c.title, exhibitCount, c.createdAt, c.visibleFrom, c.visibleTo, c.active)
                .from(c)
                .where(where)
                .orderBy(c.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<AdminCurationListResult> content = rows.stream()
                .map(r -> new AdminCurationListResult(
                        r.get(c.curationId),
                        r.get(c.title),
                        r.get(exhibitCount).intValue(),
                        r.get(c.createdAt),
                        r.get(c.visibleFrom),
                        r.get(c.visibleTo),
                        Boolean.TRUE.equals(r.get(c.active))
                )).toList();
        JPAQuery<Long> countQuery = queryFactory
                .select(c.count())
                .from(c)
                .where(where);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression containsKeyword(QCuration c, String keyword) {
        return (keyword == null || keyword.isBlank()) ? null : c.title.containsIgnoreCase(keyword.trim());
    }

    private BooleanExpression eqActive(QCuration c, Boolean active) {
        return active == null ? null : c.active.eq(active);
    }

    private BooleanExpression visibleFromGoe(QCuration c, LocalDate startDate) {
        return startDate == null ? null : c.visibleFrom.goe(startDate);
    }
    private BooleanExpression visibleToLoe(QCuration c, LocalDate endDate) {
        return endDate == null ? null : c.visibleTo.loe(endDate);
    }

}
