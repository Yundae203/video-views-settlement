package personal.streaming.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personal.streaming.application.dto.content_post_view_report.ContentPostViewReport;
import personal.streaming.application.dto.post_with_interaction.PostWithInteraction;
import personal.streaming.application.service.LoggingService;
import personal.streaming.application.service.StreamingService;

@RestController
@RequestMapping("/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;
    private final LoggingService loggingService;


    @GetMapping("/contentPosts/{contentPostId}/users/{userId}")
    public ResponseEntity<PostWithInteraction> startContent(
            HttpServletRequest request,
            @PathVariable Long contentPostId,
            @PathVariable Long userId
    ) {
        PostWithInteraction response = streamingService.getPostWithInteraction(contentPostId, userId);
        Long creatorId = response.creatorId();

        if (!isCreator(userId, creatorId)) { // 크리에이터인지 확인
            String userIp = getUserIp(request);
            loggingService.loggingViews(userIp, response.postInfo(), userId);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/contentPosts/{contentPostId}/users/{userId}")
    public void endContent(
            @PathVariable Long contentPostId,
            @PathVariable Long userId,
            @RequestBody ContentPostViewReport report
    ) {
        streamingService.saveContentPostWatchHistory(report.toContentPostWatchHistory(contentPostId, userId));
    }

    public String getUserIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private static boolean isCreator(Long userId, Long creatorId) {
        return creatorId.equals(userId);
    }
}
