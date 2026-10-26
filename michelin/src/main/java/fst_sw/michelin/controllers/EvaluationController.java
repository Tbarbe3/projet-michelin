package fst_sw.michelin.controllers;

import fst_sw.michelin.dto.evaluation.EvaluationInputDTO;
import fst_sw.michelin.dto.evaluation.EvaluationDTO;
import fst_sw.michelin.dto.ImageUrlDTO;
import fst_sw.michelin.services.EvaluationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/restaurants/{restaurantId}/comments")
public class EvaluationController {
    private final EvaluationService evaluationService;

    @GetMapping
    public List<EvaluationDTO> getEvaluations(@PathVariable int restaurantId) {
        return this.evaluationService.getEvaluations(restaurantId).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{commentId}")
    public EvaluationDTO getEvaluation(@PathVariable int restaurantId, @PathVariable int commentId) {
        return EvaluationDTO.fromEntity(this.evaluationService.getEvaluation(restaurantId, commentId));
    }

    @PostMapping
    public EvaluationDTO addEvaluation(@PathVariable int restaurantId, @Valid @RequestBody EvaluationInputDTO evaluation) {
        return EvaluationDTO.fromEntity(
                this.evaluationService.addEvaluation(restaurantId, evaluation.name, evaluation.comment, evaluation.rate));
    }

    @GetMapping("/search")
    public List<EvaluationDTO> searchEvaluations(@PathVariable int searchEvaluations, @RequestParam @NotBlank String word) {
        return this.evaluationService.searchComments(searchEvaluations, word).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }

    @DeleteMapping("/{commentId}")
    public void deleteEvaluation(@PathVariable int restaurantId, @PathVariable int commentId) {
        this.evaluationService.deleteEvaluation(restaurantId, commentId);
    }

    @PutMapping("/{commentId}/dishesPictures")
    public ImageUrlDTO addDishPicture(@PathVariable int restaurantId, @PathVariable int commentId) {
        return new ImageUrlDTO(this.evaluationService.createDishPictureUploadUrl(restaurantId, commentId));
    }

    @GetMapping("/{commentId}/dishesPictures")
    public ImageUrlDTO getDishPicture(@PathVariable int restaurantId, @PathVariable int commentId) {
        return new ImageUrlDTO(this.evaluationService.getDishPictureUploadUrl(restaurantId, commentId));
    }
}
