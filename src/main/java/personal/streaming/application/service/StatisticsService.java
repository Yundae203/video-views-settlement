package personal.streaming.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.streaming.application.common.batch.domain.repository.QContentDailyStatisticsRepository;
import personal.streaming.application.common.value.DayRange;
import personal.streaming.application.dto.statistics_aggregation.StatisticsAggregation;
import personal.streaming.content_post.domain.ContentPost;
import personal.streaming.content_post.dto.ContentPostResponse;
import personal.streaming.content_post.repository.ContentPostRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final QContentDailyStatisticsRepository qContentDailyStatisticsRepository;
    private final ContentPostRepository contentPostRepository;

    /**
     * 유저가 포스트한 모든 영상에 대한 수입을 반환한다.
     * @param date 요청 날짜
     * @param userId 영상 제작자
     * @param range 요청 날짜를 기준으로 하는 구간 (day, week, month)
     * @return 유저의 영상의 구간별 수입
     */
    public List<StatisticsAggregation> findUserContentsIncome(LocalDate date, long userId, String range){
        DayRange dayRange = DayRange.fromMode(date, range);
        return qContentDailyStatisticsRepository.findUserContentsIncome(dayRange, userId);
    }

    public List<ContentPostResponse> findViewsTopRank(LocalDate date, long userId, String range, int rank) {
        int validatedRank = validateRankLimit(rank);
        return findTopRank(date, userId, range, validatedRank,
                dayRange -> qContentDailyStatisticsRepository.findViewsTopRankBetweenDayRange(dayRange, userId, validatedRank));
    }

    public List<ContentPostResponse> findPlaytimeTopRank(LocalDate date, long userId, String range, int rank) {
        int validatedRank = validateRankLimit(rank);
        return findTopRank(date, userId, range, validatedRank,
                dayRange -> qContentDailyStatisticsRepository.findPlaytimeTopBetweenDayRange(dayRange, userId, validatedRank));
    }

    public List<ContentPostResponse> findTopRank(LocalDate date, long userId, String mode, int rank, Function<DayRange, List<Long>> function) {
        DayRange dayRange = DayRange.fromMode(date, mode);

        List<Long> contentPostIds = function.apply(dayRange); // template

        List<ContentPost> contentPosts = contentPostRepository.findAllById(contentPostIds);
        return IntStream.range(0, contentPosts.size())
                .mapToObj(i -> ContentPostResponse.of(contentPosts.get(i), i + 1))
                .toList();
    }

    private int validateRankLimit(int rank) {
        return Math.min(rank, 1000);
    }
}
