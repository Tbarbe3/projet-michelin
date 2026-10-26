package fst_sw.michelin.services;

import fst_sw.michelin.entities.RestaurantEntity;
import fst_sw.michelin.entities.EvaluationEntity;
import fst_sw.michelin.exceptions.ConflictException;
import fst_sw.michelin.exceptions.NotFoundException;
import fst_sw.michelin.repositories.S3Repository;
import fst_sw.michelin.repositories.RestaurantRepository;
import fst_sw.michelin.repositories.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Repository s3Repository;
    private final IndexService indexService;

    public EvaluationEntity addEvaluation(int restaurantId, String author, String comment, int rate) {
        RestaurantEntity restaurant = this.findRestaurantByID(restaurantId);

        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setRestaurant(restaurant);
        evaluation.setAuthor(author);
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
        this.findRestaurantByID(restaurantId); //For the exception if not found

        return this.evaluationRepository.findAllById(this.indexService.searchEvaluations(restaurantId, word));
    }

    @Transactional
    public void updateEvaluation(int restaurantId, int evaluationId, String author, boolean isAdmin, String comment, int rate) {
        EvaluationEntity evaluation = this.findEvaluationByID(restaurantId, evaluationId);
        //if (!Objects.equals(evaluation.getAuthor(), author) && !isAdmin)
        //    throw new ConflictException("Evaluation " + evaluationId + " does not belong to " + author);

        if (comment != null && !comment.isBlank())
            evaluation.setComment(comment);
        if (rate >= 0 && rate <= 3)
            evaluation.setRate(rate);

        this.evaluationRepository.save(evaluation);
        this.indexService.indexEvaluation(restaurantId, evaluation.getId(), comment);
    }

    public void deleteEvaluation(int restaurantId, int evaluationId, String author, boolean isAdmin) {
        EvaluationEntity evaluation = this.findEvaluationByID(restaurantId, evaluationId);
        //if (!Objects.equals(evaluation.getAuthor(), author) && !isAdmin)
        //    throw new ConflictException("Evaluation " + evaluationId + " does not belong to " + author);

        this.evaluationRepository.delete(evaluation);
        this.indexService.deleteEvaluation(evaluationId);
    }

    public String createDishPictureUploadUrl(int restaurantId, int evaluationId, String author, boolean isAdmin) {
        EvaluationEntity evaluation = this.findEvaluationByID(restaurantId, evaluationId);
        Collection<String> pictures = evaluation.getDishesPictures();

        //if (!Objects.equals(evaluation.getAuthor(), author) && !isAdmin)
        //    throw new ConflictException("Evaluation " + evaluationId + " does not belong to " + author);

        String key = restaurantId + "/" + evaluationId + "/" + UUID.randomUUID();
        pictures.add(key);
        this.evaluationRepository.save(evaluation);

        return this.s3Repository.getDishPictureUploadUrl(key);
    }

    public String getDishPictureUploadUrl(int restaurantId, int evaluationId, int pictureId) {
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

    public List<EvaluationEntity> getEvaluationsMe(String author) {
        return this.evaluationRepository.findByAuthor(author);
    }
}
