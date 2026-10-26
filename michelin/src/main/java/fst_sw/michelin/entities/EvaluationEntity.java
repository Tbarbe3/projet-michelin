package fst_sw.michelin.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="evaluation")
public class EvaluationEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter
    private int id;

    @NotBlank
    @Column(name = "author")
    @Size(max=50)
    @Getter @Setter
    private String author;

    @NotBlank
    @Column(name = "comment")
    @Size(max=255)
    @Getter @Setter
    private String comment;

    @Min(0)
    @Max(3)
    @Column(name = "rate")
    @Getter @Setter
    private int rate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_id")
    @Getter @Setter
    private RestaurantEntity restaurant;

    @ElementCollection
    @Getter
    @Builder.Default
    private List<String> dishesPictures = new ArrayList<>();
}
