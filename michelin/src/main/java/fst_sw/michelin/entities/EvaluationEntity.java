package fst_sw.michelin.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Entity
@Table(name="evaluation")
public class EvaluationEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter
    private int id;

    @NotBlank
    @Column(name = "name")
    @Size(max=50)
    @Getter @Setter
    private String name;

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

    @Column(name = "dishes_pictures")
    @Getter @Setter
    private Collection<String> dishesPictures;
}
