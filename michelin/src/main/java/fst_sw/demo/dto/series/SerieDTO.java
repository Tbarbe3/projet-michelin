package fst_sw.demo.dto.series;


import fst_sw.demo.entities.SerieEntity;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerieDTO {
    public UUID id;

    public String title;

    public String description;

    @Pattern(regexp = SerieEntity.RELEASE_DATE_REGEX, message = "must match YYYY/MM/DD")
    public String releaseDate;

    public static SerieDTO fromEntity(SerieEntity entity) {
        return SerieDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .releaseDate(entity.getReleaseDate())
                .build();
    }
}
