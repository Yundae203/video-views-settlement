package personal.streaming.application.common.batch.chunk.reader;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;
import personal.streaming.content_post_watch_history.repository.QDailyWatchedContentLogRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class LoggedContentIdReader implements ItemStreamReader<DailyWatchedContentLog>, StepExecutionListener {

    private final QDailyWatchedContentLogRepository qDailyWatchedContentLogRepository;

    @Value("#{jobParameters['today']}")
    private String today;
    private Long start;
    private Long end;

    @Value("${spring.chunk.size}")
    private int chunkSize;

    private final Deque<DailyWatchedContentLog> readQueue = new ArrayDeque<>();
    private LocalDate todayDate;
    private boolean hasNext = true;

    private Long lastItemId;

    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        todayDate = LocalDate.parse(today, DateTimeFormatter.ISO_LOCAL_DATE);
        Object savedItemId = stepExecution.getExecutionContext().get("savedItemId");
        if (savedItemId != null) {
            start = (long) savedItemId;
        } else {
            start = stepExecution.getExecutionContext().getLong("start");
        }
        end = stepExecution.getExecutionContext().getLong("end");
    }

    @Override
    public DailyWatchedContentLog read() {
        if(readQueue.isEmpty() && hasNext) {
            fillQueue();
        }
        return readQueue.poll();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("savedItemId", lastItemId);
    }

    private void fillQueue(){
        long limit;
        if (start + chunkSize - 1 <= end) {
            limit = chunkSize;
        } else {
            limit = end - start + 1; // 남은 데이터를 모두 처리
        }

        List<DailyWatchedContentLog> loggedContentInfos = qDailyWatchedContentLogRepository.cursorLogs(start, todayDate, limit);
        readQueue.addAll(loggedContentInfos);

        start += limit;
        saveLastItemInContext(readQueue.getLast().getId());

        if (noMoreDataInDb()) {
            log.info("No more data in DB");
            hasNext = false;
        }
    }

    private void saveLastItemInContext(long itemId) {
        lastItemId = itemId + 1L;
    }

    private boolean noMoreDataInDb() {
        log.info("start = {}, end = {}, readQueue.size = {}", start, end, readQueue.size());
        return start >= end || readQueue.size() < chunkSize;
    }
}
