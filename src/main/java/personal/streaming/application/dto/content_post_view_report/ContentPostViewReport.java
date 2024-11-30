package personal.streaming.application.dto.content_post_view_report;

import lombok.Builder;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;

import java.time.LocalDate;

@Builder
public record ContentPostViewReport(
        long watchLength,
        long pausedAt,
        long adViews
) {
    public ContentPostWatchHistory toContentPostWatchHistory(Long contentPostId,Long userId) {
        return ContentPostWatchHistory.builder()
                .watchedAt(LocalDate.now())
                .contentPostId(contentPostId)
                .userId(userId)
                .playTime(watchLength)
                .pausedAt(pausedAt)
                .adViews(adViews)
                .build();
    }
}
