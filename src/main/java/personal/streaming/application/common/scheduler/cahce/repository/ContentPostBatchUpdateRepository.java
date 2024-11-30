package personal.streaming.application.common.scheduler.cahce.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class ContentPostBatchUpdateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchUpdate(List<Map.Entry<Object, Object>> contentPostUpdateInfos) {
        String SQL = """
                UPDATE content_post
                SET total_views = total_views + ?
                WHERE id = ?
                """;
        ContentPostBatchSetter contentPostBatchSetter = new ContentPostBatchSetter(contentPostUpdateInfos);
        jdbcTemplate.batchUpdate(SQL, contentPostBatchSetter);
    }
}
