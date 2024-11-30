package personal.streaming.application.common.redis.dto;

import lombok.Builder;

@Builder
public record AbusingInfo(
        Long contentPostId,
        Long userId,
        String ip
) {

    public String getKey() {
        return String.format("users:%s:contentPosts:%s:ip:%s", userId, contentPostId, ip);
    }

}
