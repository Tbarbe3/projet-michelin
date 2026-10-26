package fst_sw.demo.dto.comments;

import fst_sw.demo.entities.CommentEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    public UUID id;

    public String name;

    public String text;

    @Min(0)
    @Max(10)
    public int rate;

    public static CommentDTO fromEntity(CommentEntity commentEntity) {
        return CommentDTO.builder()
                .id(commentEntity.getId())
                .name(commentEntity.getAuthor())
                .text(commentEntity.getContent())
                .rate(commentEntity.getRate())
                .build();
    }
}
