package personal.streaming.advertisement.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import personal.streaming.application.common.enums.Category;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;
    private String title;
    private String description;

    private String url;


    @Builder
    public Advertisement(
            Long id,
            Category category,
            String title,
            String description,
            String url
    ) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.url = url;
    }
}
