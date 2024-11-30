package personal.streaming.application.dto.post_with_interaction;

import lombok.Builder;
import personal.streaming.application.common.enums.Category;
import personal.streaming.advertisement.domain.Advertisement;

@Builder
public record AdvertisementInfo(
        Long adId,

        Category category,
        String title,
        String description,

        long length,
        String url
) {

    public static AdvertisementInfo from(Advertisement ad) {
        return AdvertisementInfo.builder()
                .adId(ad.getId())
                .category(ad.getCategory())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .url(ad.getUrl())
                .build();
    }
}
