package personal.streaming.application.port.redis;


public interface LoggingContentService {

    void addLog(Long contentPostId);

    boolean existLog(Long contentPostId);
}
