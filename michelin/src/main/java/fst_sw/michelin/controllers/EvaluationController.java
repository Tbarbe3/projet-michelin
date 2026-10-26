package fst_sw.michelin.controllers;

import fst_sw.michelin.dto.evaluation.EvaluationInputDTO;
import fst_sw.michelin.dto.evaluation.EvaluationDTO;
import fst_sw.michelin.dto.ImageUrlDTO;
import fst_sw.michelin.services.EvaluationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
    public EvaluationDTO addEvaluation(@AuthenticationPrincipal Jwt jwt, @PathVariable int restaurantId, @Valid @RequestBody EvaluationInputDTO evaluation) {
        String username = jwt.getClaimAsString("preferred_username");
        return EvaluationDTO.fromEntity(
                this.evaluationService.addEvaluation(restaurantId, username, evaluation.comment, evaluation.rate));
    }

    @GetMapping("/restaurants/{restaurantId}/evaluations/search")
    public List<EvaluationDTO> searchEvaluations(@PathVariable int restaurantId, @RequestParam @NotBlank String word) {
        return this.evaluationService.searchEvaluations(restaurantId, word).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}")
    public void updateEvaluation(@AuthenticationPrincipal Jwt jwt, @PathVariable int restaurantId, @PathVariable int evaluationId, @Valid @RequestBody EvaluationInputDTO evaluation) {
        String username = jwt.getClaimAsString("preferred_username");
        boolean isAdmin = Objects.requireNonNull(jwt.getClaimAsString("realm_access")).contains("ADMIN");
        this.evaluationService.updateEvaluation(restaurantId, evaluationId, username, isAdmin, evaluation.comment, evaluation.rate);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}")
    public void deleteEvaluation(@AuthenticationPrincipal Jwt jwt, @PathVariable int restaurantId, @PathVariable int evaluationId) {
        String username = jwt.getClaimAsString("preferred_username");
        boolean isAdmin = Objects.requireNonNull(jwt.getClaimAsString("realm_access")).contains("ADMIN");
        this.evaluationService.deleteEvaluation(restaurantId, evaluationId, username, isAdmin);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}/dishesPictures/{pictureId}")
    public ImageUrlDTO addDishPicture(@AuthenticationPrincipal Jwt jwt, @PathVariable int restaurantId, @PathVariable int evaluationId) {
        String username = jwt.getClaimAsString("preferred_username");
        boolean isAdmin = Objects.requireNonNull(jwt.getClaimAsString("realm_access")).contains("ADMIN");
        return new ImageUrlDTO(this.evaluationService.createDishPictureUploadUrl(restaurantId, evaluationId, username, isAdmin));
    }

    @GetMapping("/restaurants/{restaurantId}/evaluations/{evaluationId}/dishesPictures/{pictureId}")
    public ImageUrlDTO getDishPicture(@PathVariable int restaurantId, @PathVariable int evaluationId, @PathVariable int pictureId) {
        return new ImageUrlDTO(this.evaluationService.getDishPictureUploadUrl(restaurantId, evaluationId, pictureId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/evaluations/me")
    public List<EvaluationDTO> getEvaluationsMe(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return this.evaluationService.getEvaluationsMe(username).stream()
                .map(EvaluationDTO::fromEntity)
                .toList();
    }
}
