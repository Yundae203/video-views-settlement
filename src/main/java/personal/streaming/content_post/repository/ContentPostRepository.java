package personal.streaming.content_post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal.streaming.content_post.domain.ContentPost;

import java.util.Optional;

public interface ContentPostRepository extends JpaRepository<ContentPost, Long> {
    Optional<ContentPost> findByIdAndUserId(Long id, Long userId);
}
