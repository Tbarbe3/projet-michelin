package fst_sw.michelin.dto.evaluation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationInputDTO
{
    @NotBlank
    @Size(max=255)
    public String comment;

    @Min(0)
    @Max(3)
    @NotNull
    public Integer rate;
}
