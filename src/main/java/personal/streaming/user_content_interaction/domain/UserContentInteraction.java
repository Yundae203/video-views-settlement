package personal.streaming.user_content_interaction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(indexes = {
        @Index(name = "idx_user_id_content_post_id", columnList = "userId, contentPostId")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserContentInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentPostId;
    private Long userId;

    private long pausedAt;

    @Builder
    public UserContentInteraction(
            Long id,
            Long contentPostId,
            Long userId,
            long pausedAt
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.pausedAt = pausedAt;
    }

    public void updatePausedAt(long pausedAt) {
        this.pausedAt = pausedAt;
    }
}
