package personal.streaming.application.common.batch.dto;

import lombok.Getter;

@Getter
public class ContentTotalStatisticsDto {

    private long totalContentView = 0;
    private long totalAdView = 0;

    public void merge(ContentDailyStatisticsDto dto) {
        this.totalContentView += dto.getViews();
        this.totalAdView += dto.getAdViews();
    }
}
