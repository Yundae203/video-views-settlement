package personal.streaming.application.common.batch.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CalculateIncomeFromViews {

    // views
    private static final long VIEW_100K = 99_999;
    private static final long VIEW_500K = 4_99_999;
    private static final long VIEW_1M = 9_99_999;

    private static final long CONTENT_UNDER_100K = 10;   // 1.0 * 10
    private static final long CONTENT_UNDER_500K = 11;   // 1.1 * 10
    private static final long CONTENT_UNDER_1M = 13;     // 1.3 * 10
    private static final long CONTENT_MORE_1M = 15;      // 1.5 * 10

    private static final long AD_UNDER_100K = 100;       // 10 * 10
    private static final long AD_UNDER_500K = 120;       // 12 * 10
    private static final long AD_UNDER_1M = 150;         // 15 * 10
    private static final long AD_MORE_1M = 200;          // 20 * 10

    public static void adjustDailyStatistics(ContentTotalStatisticsDto totalStatisticsDto, ContentDailyStatisticsDto dailyStatistics) {

        long totalContentView = totalStatisticsDto.getTotalContentView() + dailyStatistics.getViews();
        long totalAdView = totalStatisticsDto.getTotalAdView() + dailyStatistics.getAdViews();

        long contentDailyIncome = calculateContentIncome(totalContentView) - calculateContentIncome(totalStatisticsDto.getTotalContentView());
        long adDailyIncome = calculateAdIncome(totalAdView) - calculateAdIncome(totalStatisticsDto.getTotalAdView());

        dailyStatistics.updateContentIncome(contentDailyIncome);
        dailyStatistics.updateAdIncome(adDailyIncome);
    }

    private static long calculateContentIncome(long views) {
        if (views <= VIEW_100K) {
            return views * CONTENT_UNDER_100K;

        } else if (views <= VIEW_500K) {
            long baseCost = VIEW_100K * CONTENT_UNDER_100K;
            long remainingViews = views - VIEW_100K;

            return baseCost + remainingViews * CONTENT_UNDER_500K;

        } else if (views <= VIEW_1M) {
            long baseCost = VIEW_100K * CONTENT_UNDER_100K +
                    (VIEW_500K - VIEW_100K) * CONTENT_UNDER_500K;
            long remainingViews = views - VIEW_500K;

            return baseCost + remainingViews * CONTENT_UNDER_1M;

        } else {
            long baseCost = VIEW_100K * CONTENT_UNDER_100K +
                    (VIEW_500K - VIEW_100K) * CONTENT_UNDER_500K +
                    (VIEW_1M - VIEW_500K) * CONTENT_UNDER_1M;
            long remainingViews = views - VIEW_1M;

            return baseCost + remainingViews * CONTENT_MORE_1M;
        }
    }

    private static long calculateAdIncome(long viewCount) {
        if (viewCount < VIEW_100K) {
            return viewCount * AD_UNDER_100K;

        } else if (viewCount < VIEW_500K) {
            long baseCost = VIEW_100K * AD_UNDER_100K;
            long remainingViews = viewCount - VIEW_100K;

            return baseCost + remainingViews * AD_UNDER_500K;

        } else if (viewCount < VIEW_1M) {
            long baseCost = VIEW_100K * AD_UNDER_100K +
                    (VIEW_500K - VIEW_100K) * AD_UNDER_500K;
            long remainingViews = viewCount - VIEW_500K;

            return baseCost + remainingViews * AD_UNDER_1M;

        } else {
            long baseCost = VIEW_100K * AD_UNDER_100K +
                    (VIEW_500K - VIEW_100K) * AD_UNDER_500K +
                    (VIEW_1M - VIEW_500K) * AD_UNDER_1M;
            long remainingViews = viewCount - VIEW_1M;

            return baseCost + remainingViews * AD_MORE_1M;
        }
    }
}
