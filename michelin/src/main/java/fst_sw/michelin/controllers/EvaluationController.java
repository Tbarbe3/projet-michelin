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
@RequestMapping("/restaurants/{restaurantId}/evaluations")
public class EvaluationController {
    private final EvaluationService evaluationService;

    @GetMapping
    public List<EvaluationDTO> getEvaluations(@PathVariable int restaurantId) {
        return this.evaluationService.getEvaluations(restaurantId).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{evaluationId}")
    public EvaluationDTO getEvaluation(@PathVariable int restaurantId, @PathVariable int evaluationId) {
        return EvaluationDTO.fromEntity(this.evaluationService.getEvaluation(restaurantId, evaluationId));
    }

    @PostMapping
    public EvaluationDTO addEvaluation(@PathVariable int restaurantId, @Valid @RequestBody EvaluationInputDTO evaluation) {
        return EvaluationDTO.fromEntity(
                this.evaluationService.addEvaluation(restaurantId, evaluation.author, evaluation.comment, evaluation.rate));
    }

    @GetMapping("/search")
    public List<EvaluationDTO> searchEvaluations(@PathVariable int restaurantId, @RequestParam @NotBlank String word) {
        return this.evaluationService.searchEvaluations(restaurantId, word).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }

    @DeleteMapping("/{evaluationId}")
    public void deleteEvaluation(@PathVariable int restaurantId, @PathVariable int evaluationId) {
        this.evaluationService.deleteEvaluation(restaurantId, evaluationId);
    }

    @PutMapping("/{evaluationId}/dishesPictures")
    public ImageUrlDTO addDishPicture(@PathVariable int restaurantId, @PathVariable int evaluationId) {
        return new ImageUrlDTO(this.evaluationService.createDishPictureUploadUrl(restaurantId, evaluationId));
    }

    @GetMapping("/{evaluationId}/dishesPictures")
    public ImageUrlDTO getDishPicture(@PathVariable int restaurantId, @PathVariable int evaluationId) {
        return new ImageUrlDTO(this.evaluationService.getDishPictureUploadUrl(restaurantId, evaluationId));
    }
}
