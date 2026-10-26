package fst_sw.demo.dto.series;


import fst_sw.demo.entities.SerieEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerieInputDTO {
    @NotBlank
    public String title;

    @NotBlank
    public String description;

    @NotBlank
    @Pattern(regexp = SerieEntity.RELEASE_DATE_REGEX, message = "must match YYYY/MM/DD")
    public String releaseDate;
}
