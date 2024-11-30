package personal.streaming.application.dto.post_with_interaction;

import lombok.Builder;
import personal.streaming.content_post.domain.ContentPost;
import personal.streaming.advertisement.domain.Advertisement;
import personal.streaming.user_content_interaction.domain.UserContentInteraction;

import java.util.List;

@Builder
public record PostWithInteraction(
        Long creatorId,
        long pausedAt,
        ContentPostInfo postInfo,
        List<AdvertisementInfo> adInfos
) {

    public static PostWithInteraction of(
            UserContentInteraction interaction,
            ContentPost post,
            List<Advertisement> ads
    ) {
        return PostWithInteraction.builder()
                .creatorId(post.getUserId())
                .pausedAt(interaction.getPausedAt())
                .postInfo(ContentPostInfo.from(post))
                .adInfos(
                        ads.stream()
                                .map(AdvertisementInfo::from)
                                .toList()
                        )
                .build();
    }
}
