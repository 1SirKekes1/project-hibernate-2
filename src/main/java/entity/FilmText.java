package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "film_text", schema = "movie", indexes = {
        @Index(name = "idx_title_description", columnList = "title, description")
})
@Getter
@Setter
public class FilmText {
    @Id
    @Column(name = "film_id", nullable = false)
    private Short id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @OneToOne
    @MapsId
    @JoinColumn(name = "film_id")
    private Film film;

}