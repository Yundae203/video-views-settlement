package personal.streaming.application.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import personal.streaming.application.common.redis.dto.AbusingInfo;
import personal.streaming.application.dto.post_with_interaction.ContentPostInfo;
import personal.streaming.application.port.redis.AbusingService;
import personal.streaming.application.port.redis.LoggingContentService;
import personal.streaming.application.port.redis.ViewCacheService;
import personal.streaming.content_post_watch_history.service.DailyWatchedContentLogService;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final AbusingService abusingService;
    private final ViewCacheService viewCacheService;
    private final LoggingContentService loggingContentService;
    private final DailyWatchedContentLogService dailyWatchedContentLogService;
    private final RedissonClient redissonClient;

    /**
     * 시청자의 어뷰징 여부를 확인한 후, 어뷰징이 아니라면 조회수를 증가시킨다.
     * @param userIp 시청자 ip
     * @param postInfo 영상 정보
     * @param userId 시청자 id
     * @see AbusingService 어뷰징 확인
     * @see ViewCacheService 조회수 증가 캐싱
     */
    public void loggingViews(String userIp, ContentPostInfo postInfo, Long userId) {
        long contentPostId = postInfo.contentPostId();
        AbusingInfo info = getAbusingKey(userIp, contentPostId, userId);

        String lockKey = "contentPostLock:" + info.toString();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock() && !abusingService.isAbusing(info)) { // 어뷰징 확인
                // 영상의 실시간 조회수 캐싱
                abusingService.setAbusing(info);
                viewCacheService.incrementView(contentPostId);

                // 시청이 발생한 영상 기록
                if(!loggingContentService.existLog(contentPostId)) {
                    loggingContent(contentPostId, postInfo.creatorId());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void loggingContent(Long contentPostId, Long creatorId) {
        String logLockKey = "logContentPostLock:" + contentPostId;
        RLock logLock = redissonClient.getLock(logLockKey);
        try {
            if (logLock.tryLock() && !loggingContentService.existLog(contentPostId)) {
                if (!existsLog(contentPostId, creatorId)) {
                    dailyWatchedContentLogService.save(contentPostId, creatorId);
                }
                loggingContentService.addLog(contentPostId); // 로그 발생 포스트 키만 저장
            }
        } finally {
            logLock.unlock();
        }
    }

    private boolean existsLog(Long contentPostId, Long creatorId) {
        return dailyWatchedContentLogService.existsByContentPostIdAndUserId(contentPostId, creatorId);
    }

    private AbusingInfo getAbusingKey(String userIp, Long contentPostId, Long userId) {
        return AbusingInfo.builder()
                .contentPostId(contentPostId)
                .userId(userId)
                .ip(userIp)
                .build();
    }
}
