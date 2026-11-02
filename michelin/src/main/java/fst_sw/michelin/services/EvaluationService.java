package fst_sw.michelin.services;

import fst_sw.michelin.entities.RestaurantEntity;
import fst_sw.michelin.entities.EvaluationEntity;
import fst_sw.michelin.exceptions.NotFoundException;
import fst_sw.michelin.exceptions.ForbiddenException;
import fst_sw.michelin.repositories.S3Repository;
import fst_sw.michelin.repositories.RestaurantRepository;
import fst_sw.michelin.repositories.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static fst_sw.michelin.config.SecurityConfig.isAdmin;

@RequiredArgsConstructor
@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Repository s3Repository;
    private final IndexService indexService;

    public EvaluationEntity addEvaluation(JwtAuthenticationToken auth, int restaurantId, String comment, Integer rate) {
        RestaurantEntity restaurant = this.findRestaurantByID(restaurantId);

        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setRestaurant(restaurant);
        evaluation.setAuthor(auth.getToken().getClaimAsString("preferred_username"));
        evaluation.setComment(comment);
        evaluation.setRate(rate);

        this.evaluationRepository.save(evaluation);
        this.indexService.indexEvaluation(restaurantId, evaluation.getId(), comment);
        return evaluation;
    }

    public List<EvaluationEntity> getEvaluations(int restaurantId) {
        return this.findRestaurantByID(restaurantId).getEvaluations().stream().toList();
    }

    public EvaluationEntity getEvaluation(int restaurantId, int evaluationId) {
        return this.findEvaluationByID(restaurantId, evaluationId);
    }

    public List<EvaluationEntity> searchEvaluations(int restaurantId, String word) {
        this.findRestaurantByID(restaurantId); // Pour throw l'exception si le resto is not found

        return this.evaluationRepository.findAllById(this.indexService.searchEvaluations(restaurantId, word));
    }

    public void updateEvaluation(JwtAuthenticationToken auth, int restaurantId, int evaluationId, String comment, Integer rate) {
        EvaluationEntity evaluation = this.findEvaluationByID(restaurantId, evaluationId);
        if (!Objects.equals(evaluation.getAuthor(), auth.getToken().getClaimAsString("preferred_username")) && !isAdmin(auth))
            throw new ForbiddenException("User " + auth.getToken().getClaimAsString("preferred_username") + " is not authorized");

        if (comment != null && !comment.isBlank())
            evaluation.setComment(comment);
        if (rate != null)
            evaluation.setRate(rate);

        this.evaluationRepository.save(evaluation);
        this.indexService.deleteEvaluation(evaluationId);
        this.indexService.indexEvaluation(restaurantId, evaluationId, evaluation.getComment());
    }

    public void deleteEvaluation(JwtAuthenticationToken auth, int restaurantId, int evaluationId) {
        EvaluationEntity evaluation = this.findEvaluationByID(restaurantId, evaluationId);
        if (!Objects.equals(evaluation.getAuthor(), auth.getToken().getClaimAsString("preferred_username")) && !isAdmin(auth))
            throw new ForbiddenException("User " + auth.getToken().getClaimAsString("preferred_username") + " is not authorized");

        this.evaluationRepository.delete(evaluation);
        this.indexService.deleteEvaluation(evaluationId);
    }

    public String createDishPictureUploadUrl(JwtAuthenticationToken auth, int restaurantId, int evaluationId) {
        EvaluationEntity evaluation = this.findEvaluationByID(restaurantId, evaluationId);
        Collection<String> pictures = evaluation.getDishesPictures();

        if (!Objects.equals(evaluation.getAuthor(), auth.getToken().getClaimAsString("preferred_username")) && !isAdmin(auth))
            throw new ForbiddenException("User " + auth.getToken().getClaimAsString("preferred_username") + " is not authorized");

        String key = restaurantId + "/" + evaluationId + "/" + UUID.randomUUID();
        pictures.add(key);
        this.evaluationRepository.save(evaluation);

        return this.s3Repository.getDishPictureUploadUrl(key);
    }

    public String getDishPictureUrl(int restaurantId, int evaluationId, int pictureId) {
        List<String> pictures = this.findEvaluationByID(restaurantId, evaluationId).getDishesPictures();
        if (pictures.isEmpty())
            throw new NotFoundException("Evaluation " + evaluationId + " has no dish picture");
        if (pictureId < 0 || pictureId >= pictures.size())
            throw new NotFoundException("Evaluation " + evaluationId + " Picture " + pictureId + " not found");

        return this.s3Repository.getDishPictureUrl(pictures.get(pictureId));
    }

    private RestaurantEntity findRestaurantByID(int restaurantId) {
        return this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant " + restaurantId + " not found"));
    }

    private EvaluationEntity findEvaluationByID(int restaurantId, int evaluationId) {
        return this.evaluationRepository.findById(evaluationId)
                .filter(c -> c.getRestaurant().getId() == restaurantId)
                .orElseThrow(() -> new NotFoundException("Evaluation " + evaluationId + " not found"));
    }

    public List<EvaluationEntity> getEvaluationsMe(JwtAuthenticationToken auth) {
        return this.evaluationRepository.findByAuthor(auth.getToken().getClaimAsString("preferred_username"));
    }
}
