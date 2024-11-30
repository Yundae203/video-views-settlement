package personal.streaming.content_post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.content_post.domain.ContentPost;
import personal.streaming.content_post.repository.ContentPostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentPostService {

    private final ContentPostRepository contentPostRepository;

    public List<ContentPost> findAllByIds(List<Long> ids) {
        return contentPostRepository.findAllById(ids);
    }

    public ContentPost findById(Long id) {
        return contentPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found ContentPost with id: " + id));
    }

    @Transactional
    public void save(ContentPost post) {
        contentPostRepository.save(post);
    }
}
