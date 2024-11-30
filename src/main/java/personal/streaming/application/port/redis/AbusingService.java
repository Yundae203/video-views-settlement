package personal.streaming.application.port.redis;

import personal.streaming.application.common.redis.dto.AbusingInfo;

public interface AbusingService {

    boolean isAbusing(AbusingInfo info);

    void setAbusing(AbusingInfo info);
}
