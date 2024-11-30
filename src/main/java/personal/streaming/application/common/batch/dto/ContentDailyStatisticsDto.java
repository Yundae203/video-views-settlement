package personal.streaming.application.common.batch.dto;

import lombok.Builder;
import lombok.Getter;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;

import java.time.LocalDate;

@Getter
public class ContentDailyStatisticsDto {

    private Long id;

    private Long contentPostId;
    private Long userId;
    private LocalDate date;

    private long views;
    private long adViews;
    private long playTime;
    private long contentIncome;
    private long adIncome;

    @Builder
    public ContentDailyStatisticsDto(
            Long id,
            Long contentPostId,
            Long userId,
            LocalDate date,
            long views,
            long adViews,
            long playTime,
            long contentIncome,
            long adIncome
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.date = date;
        this.views = views;
        this.adViews = adViews;
        this.playTime = playTime;
        this.contentIncome = contentIncome;
        this.adIncome = adIncome;
    }

    public void merge(ContentPostWatchHistory contentPostWatchHistory) {
       this.views += 1L;
       this.adViews += contentPostWatchHistory.getAdViews();
       this.playTime += contentPostWatchHistory.getPlayTime();
    }

    public void updateAdIncome(long adDailyIncome) {
       this.adIncome += adDailyIncome;
    }

    public void updateContentIncome(long contentDailyIncome) {
       this.contentIncome += contentDailyIncome;
    }
}
