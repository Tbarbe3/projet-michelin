package fst_sw.michelin.dto.evaluation;

import fst_sw.michelin.entities.EvaluationEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO {
    public int id;

    @Size(max=50)
    public String author;

    @Size(max=255)
    public String comment;

    @Min(0)
    @Max(3)
    public int rate;

    public static EvaluationDTO fromEntity(EvaluationEntity evaluationEntity) {
        return EvaluationDTO.builder()
                .id(evaluationEntity.getId())
                .author(evaluationEntity.getAuthor())
                .comment(evaluationEntity.getComment())
                .rate(evaluationEntity.getRate())
                .build();
    }
}
