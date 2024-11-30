package personal.streaming.user_content_interaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.streaming.user_content_interaction.domain.UserContentInteraction;
import personal.streaming.user_content_interaction.repository.UserContentInteractionRepository;

@Service
@RequiredArgsConstructor
public class UserContentInteractionService {

    private final UserContentInteractionRepository userContentInteractionRepository;

    public UserContentInteraction findByContentPostIdAndUserId(Long contentPostId, Long userId) {
        return userContentInteractionRepository.findByContentPostIdAndUserId(contentPostId, userId)
                .orElse(null);
    }

    public UserContentInteraction init(Long contentPostId, Long userId) {
        return save(UserContentInteraction.builder()
                .contentPostId(contentPostId)
                .userId(userId)
                .pausedAt(0L)
                .build()
        );
    }

    public UserContentInteraction save(UserContentInteraction interaction) {
        return userContentInteractionRepository.save(interaction);
    }
}
