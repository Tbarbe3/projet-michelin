package fst_sw.demo.controllers;

import fst_sw.demo.dto.comments.CommentInputDTO;
import fst_sw.demo.dto.comments.CommentDTO;
import fst_sw.demo.dto.series.ImageUrlDTO;
import fst_sw.demo.services.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/series/{serieId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDTO> getComments(@PathVariable UUID serieId) {
        return this.commentService.getComments(serieId).stream()
                .map(CommentDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{commentId}")
    public CommentDTO getComment(@PathVariable UUID serieId, @PathVariable UUID commentId) {
        return CommentDTO.fromEntity(this.commentService.getComment(serieId, commentId));
    }

    @PostMapping
    public CommentDTO addComment(@PathVariable UUID serieId, @Valid @RequestBody CommentInputDTO comment) {
        return CommentDTO.fromEntity(
                this.commentService.addComment(serieId, comment.name, comment.text, comment.rate));
    }

    @GetMapping("/search")
    public List<CommentDTO> searchComments(@PathVariable UUID serieId, @RequestParam @NotBlank String word) {
        return this.commentService.searchComments(serieId, word).stream()
                .map(CommentDTO::fromEntity)
                .toList();
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable UUID serieId, @PathVariable UUID commentId) {
        this.commentService.deleteComment(serieId, commentId);
    }

    @PutMapping("/{commentId}/screenshot")
    public ImageUrlDTO addScreenshot(@PathVariable UUID serieId, @PathVariable UUID commentId) {
        return new ImageUrlDTO(this.commentService.createScreenshotUploadUrl(serieId, commentId));
    }

    @GetMapping("/{commentId}/screenshot")
    public ImageUrlDTO getScreenshot(@PathVariable UUID serieId, @PathVariable UUID commentId) {
        return new ImageUrlDTO(this.commentService.getScreenshotUrl(serieId, commentId));
    }
}
