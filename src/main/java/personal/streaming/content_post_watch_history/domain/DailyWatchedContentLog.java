package personal.streaming.content_post_watch_history.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyWatchedContentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentPostId;
    private Long userId;
    private LocalDate date;

    @Builder
    public DailyWatchedContentLog(
            Long id,
            Long contentPostId,
            Long userId,
            LocalDate date
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.date = date;
    }
}
