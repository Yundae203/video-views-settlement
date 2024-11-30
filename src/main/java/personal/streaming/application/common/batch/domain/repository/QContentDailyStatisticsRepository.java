package personal.streaming.application.common.batch.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;
import personal.streaming.application.common.value.DayRange;
import personal.streaming.application.dto.statistics_aggregation.StatisticsAggregation;

import java.util.List;

import static personal.streaming.application.common.batch.domain.QContentDailyStatistics.contentDailyStatistics;

@Repository
public class QContentDailyStatisticsRepository {

    private final JPAQueryFactory queryFactory;

    public QContentDailyStatisticsRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Value("${spring.chunk.size}")
    private int chunkSize;

    public List<ContentDailyStatisticsDto> cursorContentsLog(long userId, long contentPostId, long id) {
        return queryFactory.select(Projections.constructor(
                ContentDailyStatisticsDto.class,
                contentDailyStatistics.id,
                contentDailyStatistics.contentPostId,
                contentDailyStatistics.userId,
                contentDailyStatistics.date,
                contentDailyStatistics.views,
                contentDailyStatistics.adViews,
                contentDailyStatistics.playTime,
                contentDailyStatistics.contentIncome,
                contentDailyStatistics.adIncome
                ))
                .from(contentDailyStatistics)
                .where(
                        contentDailyStatistics.userId.eq(userId),
                        contentDailyStatistics.contentPostId.eq(contentPostId),
                        contentDailyStatistics.id.gt(id)
                )
                .limit(chunkSize)
                .fetch();
    }

    public List<StatisticsAggregation> findUserContentsIncome(DayRange range, long userId) {

        NumberExpression<Long> contentIncomeSum = contentDailyStatistics.contentIncome.sum();
        NumberExpression<Long> adIncomeSum = contentDailyStatistics.adIncome.sum();
        NumberExpression<Long> totalIncome = contentIncomeSum.add(adIncomeSum);

        return queryFactory
                .select(Projections.constructor(
                        StatisticsAggregation.class,
                        contentDailyStatistics.contentPostId,
                        contentIncomeSum,
                        adIncomeSum,
                        totalIncome
                ))
                .from(contentDailyStatistics)
                .where(
                        contentDailyStatistics.date.between(range.start(), range.end()),
                        contentDailyStatistics.userId.eq(userId)
                )
                .groupBy(contentDailyStatistics.contentPostId)
                .orderBy(totalIncome.desc())
                .fetch();
    }

    public List<Long> findViewsTopRankBetweenDayRange(DayRange range, long userId, int rank) {
        return queryFactory
                .select(contentDailyStatistics.contentPostId)
                .from(contentDailyStatistics)
                .where(
                        contentDailyStatistics.date.between(range.start(), range.end()),
                        contentDailyStatistics.userId.eq(userId)
                )
                .groupBy(contentDailyStatistics.contentPostId)
                .orderBy(contentDailyStatistics.views.sum().desc())
                .limit(rank)
                .fetch();
    }

    public List<Long> findPlaytimeTopBetweenDayRange(DayRange range, long userId, int rank) {
        return queryFactory
                .select(contentDailyStatistics.contentPostId)
                .from(contentDailyStatistics)
                .where(
                        contentDailyStatistics.date.between(range.start(), range.end()),
                        contentDailyStatistics.userId.eq(userId)
                )
                .groupBy(contentDailyStatistics.contentPostId)
                .orderBy(contentDailyStatistics.playTime.sum().desc())
                .limit(rank)
                .fetch();
    }
}
