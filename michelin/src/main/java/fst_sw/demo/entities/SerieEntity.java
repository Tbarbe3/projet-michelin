package fst_sw.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Collection;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "serie")
public class SerieEntity {
    public static final String RELEASE_DATE_REGEX = "^\\d{4}/(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])$";

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter
    private UUID id;

    @NotBlank
    @Column(name = "title")
    @Getter @Setter
    private String title;

    @NotBlank
    @Column(name = "description")
    @Getter @Setter
    private String description;

    @Column(name = "cover")
    @Getter @Setter
    private String cover;

    @OneToMany(mappedBy = "serie")
    @Getter
    private Collection<CommentEntity> comments;

    @NotBlank
    @Pattern(regexp = RELEASE_DATE_REGEX)
    @Column(name = "releaseDate")
    @Getter @Setter
    private String releaseDate;
}
