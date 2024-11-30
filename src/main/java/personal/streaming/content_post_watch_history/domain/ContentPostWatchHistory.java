package personal.streaming.content_post_watch_history.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_watched_at_content_post_id", columnList = "watchedAt, contentPostId")
        }
)
public class ContentPostWatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long contentPostId;

    private LocalDate watchedAt;
    @Min(0)
    private long adViews;

    @Min(0)
    private long playTime;
    @Min(0)
    private long pausedAt;

    @Builder
    public ContentPostWatchHistory(
            Long id,
            Long userId,
            Long contentPostId,
            LocalDate watchedAt,
            long adViews,
            long playTime,
            long pausedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.contentPostId = contentPostId;
        this.watchedAt = watchedAt;
        this.adViews = adViews;
        this.playTime = playTime;
        this.pausedAt = pausedAt;
    }
}
