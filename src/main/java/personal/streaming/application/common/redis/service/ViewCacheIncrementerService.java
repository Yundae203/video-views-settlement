package personal.streaming.application.common.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import personal.streaming.application.port.redis.ViewCacheService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCacheIncrementerService implements ViewCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "viewCounts:time:";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    /**
     * key = viewCounts:time:202410251940
     * value = HashMap { "postId" : "count", "postId" : "count" }
     */

    @Override
    public long incrementView(Long contentPostId) {
        log.info("increase");
        return redisTemplate.opsForHash().increment(getKey(), String.valueOf(contentPostId), 1);
    }

    public String getKey() {
        return PREFIX + getTime();
    }

    public String getTime() {
        return LocalDateTime.now().format(formatter);
    }
}
