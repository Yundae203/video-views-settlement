package personal.streaming.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personal.streaming.application.dto.statistics_aggregation.StatisticsAggregation;
import personal.streaming.application.service.StatisticsService;
import personal.streaming.content_post.dto.ContentPostResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/users/{userId}/contents/views")
    public ResponseEntity<List<ContentPostResponse>> rankedTopViewsContents(
            @PathVariable long userId,
            @RequestParam LocalDate date,
            @RequestParam String range,
            @RequestParam int rank
    ) {
        List<ContentPostResponse> viewsTopRank = statisticsService.findViewsTopRank(date, userId, range, rank);
        return ResponseEntity.ok(viewsTopRank);
    }

    @GetMapping("/users/{userId}/contents/playtime")
    public ResponseEntity<List<ContentPostResponse>> rankedTopPlaytimeContents(
            @PathVariable long userId,
            @RequestParam LocalDate date,
            @RequestParam String range,
            @RequestParam int rank
    ) {
        List<ContentPostResponse> viewsTopRank = statisticsService.findPlaytimeTopRank(date, userId, range, rank);
        return ResponseEntity.ok(viewsTopRank);
    }

    @GetMapping("/users/{userId}/contents/income")
    public ResponseEntity<List<StatisticsAggregation>> findUserContentsIncome(
        @PathVariable long userId,
        @RequestParam LocalDate date,
        @RequestParam String range
    ){
        List<StatisticsAggregation> userContentsIncome = statisticsService.findUserContentsIncome(date, userId, range);
        return ResponseEntity.ok(userContentsIncome);
    }


}
