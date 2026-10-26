package fst_sw.michelin.dto.evaluation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationInputDTO
{
    @NotBlank
    @Size(max=50)
    public String author;

    @NotBlank
    @Size(max=255)
    public String comment;

    @Min(0)
    @Max(10)
    public int rate;
}
