package personal.streaming.content_post.dto;

import lombok.Builder;
import personal.streaming.application.common.enums.Category;
import personal.streaming.content_post.domain.ContentPost;

@Builder
public record ContentPostResponse(
    Long id,
    Long userId,
    String title,
    String description,
    Category category,
    String url,
    Long length,
    Long totalViews,
    Integer rank
) {

    public static ContentPostResponse of(ContentPost contentPost, int rank) {
        return ContentPostResponse.builder()
                .id(contentPost.getId())
                .userId(contentPost.getUserId())
                .title(contentPost.getTitle())
                .description(contentPost.getDescription())
                .category(contentPost.getCategory())
                .url(contentPost.getUrl())
                .length(contentPost.getLength())
                .totalViews(contentPost.getTotalViews())
                .rank(rank)
                .build();
    }
}
