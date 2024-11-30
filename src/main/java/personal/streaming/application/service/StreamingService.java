package personal.streaming.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.application.dto.post_with_interaction.PostWithInteraction;
import personal.streaming.application.port.advertisement.AdvertisementService;
import personal.streaming.content_post.domain.ContentPost;
import personal.streaming.content_post.service.ContentPostService;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;
import personal.streaming.content_post_watch_history.service.ContentPostWatchHistoryService;
import personal.streaming.advertisement.domain.Advertisement;
import personal.streaming.user_content_interaction.domain.UserContentInteraction;
import personal.streaming.user_content_interaction.service.UserContentInteractionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private final UserContentInteractionService userContentInteractionService;
    private final ContentPostService contentPostService;
    private final AdvertisementService advertisementService;
    private final ContentPostWatchHistoryService contentPostWatchHistoryService;

    /**
     * 시청자가 요청한 영상과 영상 길이에 따른 광고를 삽입한 후,
     * 해당 영상과 시청자의 상호작용 정보를 반환한다.
     * @param postId 요청 영상 id
     * @param userId 시청자 id
     * @return 영상 & 광고 & 상호작용 정보
     */
    public PostWithInteraction getPostWithInteraction(Long postId, Long userId) {
        UserContentInteraction interaction = userContentInteractionService.findByContentPostIdAndUserId(postId, userId);
        if (interaction == null) {
            interaction = userContentInteractionService.init(postId, userId);
        }
        ContentPost post = contentPostService.findById(postId);
        List<Advertisement> ads = advertisementService.insertAdvertisements(post.getCategory(), post.getLength());

        return PostWithInteraction.of(interaction, post, ads);
    }

    /**
     * 시청이 완료된 영상의 로그를 저장하고 유저의 마지막 시청 기록을 업데이트 한다.
     * @param history 유저의 시청 기록
     */
    @Transactional
    public void saveContentPostWatchHistory(ContentPostWatchHistory history) {
        contentPostWatchHistoryService.save(history);

        UserContentInteraction interaction = userContentInteractionService
                .findByContentPostIdAndUserId(history.getContentPostId(), history.getUserId());
        interaction.updatePausedAt(history.getPausedAt());
        userContentInteractionService.save(interaction);
    }
}
