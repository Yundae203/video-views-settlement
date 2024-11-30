package personal.streaming.content_post_watch_history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;

public interface DailyWatchedContentLogRepository extends JpaRepository<DailyWatchedContentLog, Long> {

    public boolean existsByContentPostIdAndUserId(Long contentPostId, Long userId);
}
