package personal.streaming.content_post_watch_history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;

public interface ContentPostWatchHistoryRepository extends JpaRepository<ContentPostWatchHistory, Long> {
}