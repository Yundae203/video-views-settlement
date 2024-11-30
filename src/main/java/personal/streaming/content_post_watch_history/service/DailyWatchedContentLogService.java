package personal.streaming.content_post_watch_history.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;
import personal.streaming.content_post_watch_history.repository.DailyWatchedContentLogRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyWatchedContentLogService {

    private final DailyWatchedContentLogRepository dailyWatchedContentLogRepository;

    public boolean existsByContentPostIdAndUserId(Long contentPostId, Long creatorId) {
        return dailyWatchedContentLogRepository.existsByContentPostIdAndUserId(contentPostId, creatorId);
    }

    public void save(Long contentPostId, Long creatorId) {
        DailyWatchedContentLog dailyWatchedContentLog = DailyWatchedContentLog.builder()
                .contentPostId(contentPostId)
                .userId(creatorId)
                .date(LocalDate.now())
                .build();
        dailyWatchedContentLogRepository.save(dailyWatchedContentLog);
    }
}
