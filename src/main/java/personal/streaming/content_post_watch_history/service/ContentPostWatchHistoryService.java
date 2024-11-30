package personal.streaming.content_post_watch_history.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;
import personal.streaming.content_post_watch_history.repository.ContentPostWatchHistoryRepository;

@Service
@RequiredArgsConstructor
public class ContentPostWatchHistoryService {

    private final ContentPostWatchHistoryRepository contentPostWatchHistoryRepository;

    @Transactional
    public void save(ContentPostWatchHistory contentPostWatchHistory) {
        contentPostWatchHistoryRepository.save(contentPostWatchHistory);
    }
}
