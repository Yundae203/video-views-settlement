package personal.streaming.application.common.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.redis.dto.AbusingInfo;
import personal.streaming.application.port.redis.AbusingService;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AbusingByUserIdAndIpService implements AbusingService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean isAbusing(AbusingInfo info) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(info.getKey()));
    }

    @Override
    public void setAbusing(AbusingInfo info) {
        redisTemplate.opsForValue().set(info.getKey(), "1", 30, TimeUnit.SECONDS);
    }
}
