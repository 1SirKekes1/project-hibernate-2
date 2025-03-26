package entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "film_text", schema = "movie", indexes = {
        @Index(name = "idx_title_description", columnList = "title, description")
})
public class FilmText {
    @Id
    @Column(name = "film_id", nullable = false)
    private Short id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    @Type(type = "text")
    private String description;

    @OneToOne
    @MapsId
    @JoinColumn(name = "film_id")
    private Film film;

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}