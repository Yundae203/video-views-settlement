package personal.streaming.application.common.batch.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentDailyStatisticsBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void insert(final List<ContentDailyStatisticsDto> contentDailyStatisticsDtoList) {
        String SQL = """
                INSERT INTO content_daily_statistics (
                                      content_post_id,
                                      user_id,
                                      date,
                                      views,
                                      content_income,
                                      ad_income,
                                      play_time,
                                      ad_views
                      )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ContentDailyStatisticsDto dto = contentDailyStatisticsDtoList.get(i);
                ps.setLong(1, dto.getContentPostId());
                ps.setLong(2, dto.getUserId());
                ps.setObject(3, dto.getDate()); // LocalDate 타입은 setObject로 설정
                ps.setLong(4, dto.getViews());
                ps.setLong(5, dto.getContentIncome());
                ps.setLong(6, dto.getAdIncome());
                ps.setLong(7, dto.getPlayTime());
                ps.setLong(8, dto.getAdViews());
            }

            @Override
            public int getBatchSize() {
                return contentDailyStatisticsDtoList.size();
            }
        });
    }
}
