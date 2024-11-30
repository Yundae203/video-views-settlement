package personal.streaming.content_post_watch_history.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;

import java.time.LocalDate;
import java.util.List;

import static personal.streaming.content_post_watch_history.domain.QContentPostWatchHistory.contentPostWatchHistory;

@Repository
public class QContentPostWatchHistoryRepository {

    private final JPAQueryFactory queryFactory;
    @Value("${spring.batch.size}")
    private int batchSize;

    public QContentPostWatchHistoryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<ContentPostWatchHistory> findAllByContentId(long contentId, LocalDate watchedAt, long id) {
        return queryFactory
                .selectFrom(contentPostWatchHistory)
                .where(
                        contentPostWatchHistory.contentPostId.eq(contentId),
                        contentPostWatchHistory.watchedAt.eq(watchedAt),
                        contentPostWatchHistory.id.gt(id)
                )
                .offset(0)
                .limit(batchSize)
                .fetch();
    }
}
