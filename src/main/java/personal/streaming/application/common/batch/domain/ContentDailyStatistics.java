package personal.streaming.application.common.batch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_date_id_user_id_content_post_id", columnList = "date, userId, contentPostId"),
                @Index(name = "idx_user_id_content_post_id_id", columnList = "userId, contentPostId, id"),
                @Index(name = "idx_date", columnList = "date")
        }
)
public class ContentDailyStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentPostId;
    private Long userId;

    private LocalDate date;
    private Long views;
    private Long contentIncome;
    private Long adIncome;
    private Long playTime;
    private Long adViews;

    @Builder
    public ContentDailyStatistics(
            Long id,
            Long contentPostId,
            Long userId,
            LocalDate date,
            Long views,
            Long contentIncome,
            Long adIncome,
            Long playTime,
            Long adViews
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.date = date;
        this.views = views;
        this.contentIncome = contentIncome;
        this.adIncome = adIncome;
        this.playTime = playTime;
        this.adViews = adViews;
    }
}
