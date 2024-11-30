package personal.streaming.user_content_interaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal.streaming.user_content_interaction.domain.UserContentInteraction;

import java.util.Optional;

public interface UserContentInteractionRepository extends JpaRepository<UserContentInteraction, Long> {

    Optional<UserContentInteraction> findByContentPostIdAndUserId(Long contentPostId,Long userId);
}
