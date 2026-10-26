package fst_sw.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="comment")
public class CommentEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter
    private UUID id;

    @NotBlank
    @Column(name = "author")
    @Getter @Setter
    private String author;

    @NotBlank
    @Column(name = "content")
    @Getter @Setter
    private String content;

    @Min(0)
    @Max(10)
    @Column(name = "rate")
    @Getter @Setter
    private int rate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "serie_id")
    @Getter @Setter
    private SerieEntity serie;

    @Column(name = "screenshot")
    @Getter @Setter
    private String screenshot;
}
