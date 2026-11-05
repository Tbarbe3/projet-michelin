package fst_sw.michelin.controllers;

import fst_sw.michelin.dto.evaluation.EvaluationInputDTO;
import fst_sw.michelin.dto.evaluation.EvaluationDTO;
import fst_sw.michelin.dto.ImageUrlDTO;
import fst_sw.michelin.dto.evaluation.EvaluationUpdateDTO;
import fst_sw.michelin.services.EvaluationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EvaluationController {
    private final EvaluationService evaluationService;

    @GetMapping("/restaurants/{restaurantId}/evaluations")
    public List<EvaluationDTO> getEvaluations(@PathVariable int restaurantId) {
        return this.evaluationService.getEvaluations(restaurantId).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }

    @GetMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}")
    public EvaluationDTO getEvaluation(@PathVariable int restaurantId, @PathVariable int evaluationId) {
        return EvaluationDTO.fromEntity(this.evaluationService.getEvaluation(restaurantId, evaluationId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/restaurants/{restaurantId}/evaluations")
    public EvaluationDTO addEvaluation(JwtAuthenticationToken auth, @PathVariable int restaurantId, @Valid @RequestBody EvaluationInputDTO evaluation) {
        return EvaluationDTO.fromEntity(this.evaluationService.addEvaluation(auth, restaurantId, evaluation.comment, evaluation.rate));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}")
    public void updateEvaluation(JwtAuthenticationToken auth, @PathVariable int restaurantId, @PathVariable int evaluationId, @Valid @RequestBody EvaluationUpdateDTO evaluation) {
        this.evaluationService.updateEvaluation(auth, restaurantId, evaluationId, evaluation.comment, evaluation.rate);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}")
    public void deleteEvaluation(JwtAuthenticationToken auth, @PathVariable int restaurantId, @PathVariable int evaluationId) {
        this.evaluationService.deleteEvaluation(auth, restaurantId, evaluationId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}/dishesPictures")
    public ImageUrlDTO addDishPicture(JwtAuthenticationToken auth, @PathVariable int restaurantId, @PathVariable int evaluationId) {
        return new ImageUrlDTO(this.evaluationService.createDishPictureUploadUrl(auth, restaurantId, evaluationId));
    }

    @GetMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}/dishesPictures/{pictureId}")
    public ImageUrlDTO getDishPicture(@PathVariable int restaurantId, @PathVariable int evaluationId, @PathVariable int pictureId) {
        return new ImageUrlDTO(this.evaluationService.getDishPictureUrl(restaurantId, evaluationId, pictureId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/evaluations/me")
    public List<EvaluationDTO> getEvaluationsMe(JwtAuthenticationToken auth) {
        return this.evaluationService.getEvaluationsMe(auth).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }

    @GetMapping("/evaluations/search")
    public List<EvaluationDTO> searchEvaluations(@RequestParam @NotBlank String word) {
        return this.evaluationService.searchEvaluations(word).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }
}
