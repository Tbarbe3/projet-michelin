package fst_sw.demo.dto.comments;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentInputDTO
{
    @NotBlank
    public String name;

    @NotBlank
    public String text;

    @Min(0)
    @Max(10)
    public int rate;
}
