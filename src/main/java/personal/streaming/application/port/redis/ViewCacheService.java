package personal.streaming.application.port.redis;

public interface ViewCacheService {

    long incrementView(Long contentPostId);

}
