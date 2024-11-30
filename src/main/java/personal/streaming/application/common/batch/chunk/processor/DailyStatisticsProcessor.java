package personal.streaming.application.common.batch.chunk.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.application.common.batch.domain.repository.QContentDailyStatisticsRepository;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.util.CalculateIncomeFromViews;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;
import personal.streaming.content_post_watch_history.repository.QContentPostWatchHistoryRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyStatisticsProcessor implements ItemProcessor<DailyWatchedContentLog, ContentDailyStatisticsDto> {

    private final QContentPostWatchHistoryRepository qContentPostWatchHistoryRepository;
    private final QContentDailyStatisticsRepository qContentDailyStatisticsRepository;

    @Value("${spring.batch.size}")
    private int batchSize;

    @Override
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ContentDailyStatisticsDto process(@NonNull DailyWatchedContentLog item) {

        long start = System.currentTimeMillis();
        // Data prepare
        ContentTotalStatisticsDto totalDto = createTotalStatistics(item);
        ContentDailyStatisticsDto dailyDto = createDailyStatistics(item);
        long end = System.currentTimeMillis();

        // 수입 정산
        CalculateIncomeFromViews.adjustDailyStatistics(totalDto, dailyDto);
        log.info("DB duration = {}, contentId = {} views = {}", end - start, dailyDto.getContentPostId(), dailyDto.getViews());

        return dailyDto;
    }

    private ContentTotalStatisticsDto createTotalStatistics(DailyWatchedContentLog item) {
        ContentTotalStatisticsDto totalDto = new ContentTotalStatisticsDto();

        List<ContentDailyStatisticsDto> statistics;
        long cursor = 0L;
        do {
            statistics = qContentDailyStatisticsRepository.cursorContentsLog(
                    item.getUserId(), item.getContentPostId(), cursor
            );

            if (!statistics.isEmpty()) {
                statistics.forEach(totalDto::merge);
                cursor = statistics.getLast().getId();
            }
        } while(!statistics.isEmpty() && statistics.size() == batchSize);
        return totalDto;
    }

    private ContentDailyStatisticsDto createDailyStatistics(DailyWatchedContentLog item) {
        ContentDailyStatisticsDto dto = dailyStatisticsDtoInit(item);

        List<ContentPostWatchHistory> histories;
        long cursor = 0L;
        do {
            histories = qContentPostWatchHistoryRepository.findAllByContentId(
                    item.getContentPostId(), item.getDate(), cursor
            );

            if (!histories.isEmpty()) {
                histories.forEach(dto::merge); // merge data
                cursor = histories.getLast().getId();
            }
        } while (!histories.isEmpty() && histories.size() == batchSize);
        return dto;
    }

    private static ContentDailyStatisticsDto dailyStatisticsDtoInit(DailyWatchedContentLog item) {
        return ContentDailyStatisticsDto.builder()
                .contentPostId(item.getContentPostId())
                .userId(item.getUserId())
                .date(item.getDate())
                .build();
    }
}
