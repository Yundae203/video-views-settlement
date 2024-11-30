package personal.streaming.application.dto.post_with_interaction;

import lombok.Builder;
import personal.streaming.application.common.enums.Category;
import personal.streaming.content_post.domain.ContentPost;

@Builder
public record ContentPostInfo(
        Long contentPostId,
        Long creatorId,

        Category category,
        String title,
        String description,

        long length,
        String url,
        long totalViews
) {
    public static ContentPostInfo from(ContentPost contentPost) {
        return ContentPostInfo.builder()
                .contentPostId(contentPost.getId())
                .creatorId(contentPost.getUserId())
                .category(contentPost.getCategory())
                .title(contentPost.getTitle())
                .description(contentPost.getDescription())
                .length(contentPost.getLength())
                .url(contentPost.getUrl())
                .totalViews(contentPost.getTotalViews())
                .build();
    }
}
