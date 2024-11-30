package personal.streaming.application.common.scheduler.cahce.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ContentPostBatchSetter implements BatchPreparedStatementSetter {

    private final List<Map.Entry<Object, Object>> contentPostUpdateInfos;

    public ContentPostBatchSetter(List<Map.Entry<Object, Object>> contentPostUpdateInfos) {
        this.contentPostUpdateInfos = contentPostUpdateInfos;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Map.Entry<Object, Object> info = contentPostUpdateInfos.get(i);
        ps.setLong(1, Long.parseLong((String) info.getValue())); // totalViews
        ps.setLong(2, Long.parseLong((String) info.getKey()));   // WHERE 조건으로 id 사용
    }

    @Override
    public int getBatchSize() {
        return contentPostUpdateInfos.size();
    }
}