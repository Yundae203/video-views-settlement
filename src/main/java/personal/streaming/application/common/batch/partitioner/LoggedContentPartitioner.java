package personal.streaming.application.common.batch.partitioner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;
import personal.streaming.content_post_watch_history.repository.QDailyWatchedContentLogRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class LoggedContentPartitioner implements Partitioner {

    private final QDailyWatchedContentLogRepository qdailyWatchedContentLogRepository;
    @Value("#{jobParameters['today']}")
    private String today;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        LocalDate date = LocalDate.parse(today, DateTimeFormatter.ISO_LOCAL_DATE);

        DailyWatchedContentLog firstLog = qdailyWatchedContentLogRepository.findFirstLog(0, date);
        DailyWatchedContentLog lastLog = qdailyWatchedContentLogRepository.findLastLog(0, date);

        long min = firstLog == null ? 0 : firstLog.getId();
        long max = lastLog == null ? 0 : lastLog.getId();
        long total = max - min + 1;
        long targetSize = total / gridSize;

        Map<String, ExecutionContext> result = new HashMap<>();

        long start = min;
        long end = min + targetSize - 1;

        for (int i=1; i<=gridSize; i++) {
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + i, value);

            if (i == gridSize && end < max) {
                end = max;
            }

            log.info("start = {}, end = {}", start, end);
            value.putLong("start", start);
            value.putLong("end", end);
            start += targetSize;
            end += targetSize;
        }

        return result;
    }
}
