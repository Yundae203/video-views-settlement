package personal.streaming.application.common.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;
import personal.streaming.content_post_watch_history.repository.QDailyWatchedContentLogRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyStatisticsStepListener implements StepExecutionListener {

    private final QDailyWatchedContentLogRepository qdailyWatchedContentLogRepository;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String today = stepExecution.getJobParameters().getString("today");
        LocalDate todayDate = LocalDate.parse(today, DateTimeFormatter.ISO_LOCAL_DATE);

        DailyWatchedContentLog firstLog = qdailyWatchedContentLogRepository.findFirstLog(0, todayDate);
        DailyWatchedContentLog lastLog = qdailyWatchedContentLogRepository.findLastLog(0, todayDate);

        stepExecution.getExecutionContext().put("start", firstLog.getId());
        stepExecution.getExecutionContext().put("end", lastLog.getId());

        log.info("firstKey = {}, lastKey = {}", firstLog.getId(), lastLog.getId());
        log.info("totalLog = {}", lastLog.getId() - firstLog.getId() + 1);
    }
}
