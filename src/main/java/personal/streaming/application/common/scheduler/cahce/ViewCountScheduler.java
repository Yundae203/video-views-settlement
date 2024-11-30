package personal.streaming.application.common.scheduler.cahce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.scheduler.cahce.repository.ContentPostBatchUpdateRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final ContentPostBatchUpdateRepository contentPostBatchUpdateRepository;

    private static final String PREFIX = "viewCounts:time:";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    @Scheduled(cron = "5 * * * * *")
    public void calculateViewCounts() {
        batchProcessing();
    }

    private void batchProcessing() {
        List<Map.Entry<Object, Object>> updateInfos = scanEntries(); // 레디스에서 데이터 조회

        while (!updateInfos.isEmpty()) {
            log.info("key amount = {}", updateInfos.size());
            contentPostBatchUpdateRepository.batchUpdate(updateInfos); // 배치 업데이트

            while (!updateInfos.isEmpty()) {
                Map.Entry<Object, Object> entry = updateInfos.removeFirst();
                redisTemplate.boundHashOps(getKey()).delete(entry.getKey()); // 레디스에서 키 삭제
                log.info("removedKey = {}", entry.getKey().toString());
            }

            updateInfos = scanEntries();
        }
    }

    private List<Map.Entry<Object, Object>> scanEntries() {
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .count(1000)
                .build();
        List<Map.Entry<Object, Object>> updateInfos = new ArrayList<>();

        try (
                Cursor<Map.Entry<Object, Object>> cursor =
                        redisTemplate.boundHashOps(getKey()).scan(scanOptions)
        ) {
            while (cursor.hasNext()) {
                updateInfos.add(cursor.next()); // postId : views
            }
        }
        return updateInfos;
    }

    public String getKey() {
        return PREFIX + getTime();
    }

    public String getTime() {
        return LocalDateTime.now().minusMinutes(1).format(formatter);
    }
}
