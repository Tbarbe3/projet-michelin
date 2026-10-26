package fst_sw.michelin.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant")
public class RestaurantEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter
    private int id;

    @NotBlank
    @Column(name = "name")
    @Size(max=90)
    @Getter @Setter
    private String name;

    @NotBlank
    @Column(name = "address")
    @Getter @Setter
    @Size(max=255)
    private String address;

    @Column(name = "image")
    @Getter @Setter
    private String image;

    @OneToMany(mappedBy = "restaurant")
    @Getter
    @Builder.Default
    private Collection<EvaluationEntity> evaluations = new ArrayList<>();
}
