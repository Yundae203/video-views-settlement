package personal.streaming.application.common.batch.chunk.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.batch.domain.repository.ContentDailyStatisticsBatchRepository;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyStatisticsBatchWriter implements ItemWriter<ContentDailyStatisticsDto> {

    private final ContentDailyStatisticsBatchRepository contentDailyStatisticsBatchRepository;

    @Override
    public void write(Chunk<? extends ContentDailyStatisticsDto> chunk) {
        List<ContentDailyStatisticsDto> dailyStatistics = (List<ContentDailyStatisticsDto>) chunk.getItems();
        contentDailyStatisticsBatchRepository.insert(dailyStatistics);
    }
}
