package personal.streaming.content_post_watch_history.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;

import java.time.LocalDate;
import java.util.List;

import static personal.streaming.content_post_watch_history.domain.QDailyWatchedContentLog.dailyWatchedContentLog;

@Repository
public class QDailyWatchedContentLogRepository {

    private final JPAQueryFactory queryFactory;

    public QDailyWatchedContentLogRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public DailyWatchedContentLog findFirstLog(long id, LocalDate date) {
        return queryFactory
                .selectFrom(dailyWatchedContentLog)
                .where(
                        dailyWatchedContentLog.date.eq(date),
                        dailyWatchedContentLog.id.gt(id)
                )
                .limit(1)
                .fetchOne();
    }

    public DailyWatchedContentLog findLastLog(long id, LocalDate date) {
        return queryFactory
                .selectFrom(dailyWatchedContentLog)
                .where(
                        dailyWatchedContentLog.date.eq(date),
                        dailyWatchedContentLog.id.gt(id)
                )
                .orderBy(dailyWatchedContentLog.id.desc())
                .limit(1)
                .fetchOne();
    }

    public List<DailyWatchedContentLog> cursorLogs(long id, LocalDate date, long limit) {
        return queryFactory
                .selectFrom(dailyWatchedContentLog)
                .where(
                        dailyWatchedContentLog.date.eq(date),
                        dailyWatchedContentLog.id.goe(id)
                )
                .offset(0)
                .limit(limit)
                .fetch();
    }
}
