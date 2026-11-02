package fst_sw.michelin.services;

import fst_sw.michelin.TestData;
import fst_sw.michelin.entities.EvaluationEntity;
import fst_sw.michelin.exceptions.ForbiddenException;
import fst_sw.michelin.exceptions.NotFoundException;
import fst_sw.michelin.repositories.EvaluationRepository;
import fst_sw.michelin.repositories.RestaurantRepository;
import fst_sw.michelin.repositories.S3Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static fst_sw.michelin.TestToken.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {
    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private S3Repository s3Repository;

    @Mock
    private IndexService indexService;

    @InjectMocks
    private EvaluationService evaluationService;

    private final TestData data = new TestData();

    @Test
    void addEvaluation() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));

        EvaluationEntity created = this.evaluationService.addEvaluation(user(LUCIEN), this.data.r1.getId(), "Très bon", 2);

        assertThat(created.getAuthor()).isEqualTo(LUCIEN);
        assertThat(created.getComment()).isEqualTo("Très bon");
        assertThat(created.getRate()).isEqualTo(2);
        assertThat(created.getRestaurant()).isSameAs(this.data.r1);
        verify(this.evaluationRepository).save(created);
        verify(this.indexService).indexEvaluation(this.data.r1.getId(), created.getId(), "Très bon");
    }

    @Test
    void addEvaluation_notFound() {
        assertThatThrownBy(() -> this.evaluationService.addEvaluation(user(LUCIEN), 999, "Commentaire", 0))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEvaluations() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));

        assertThat(this.evaluationService.getEvaluations(this.data.r1.getId())).containsExactly(this.data.e1, this.data.e2);
    }

    @Test
    void getEvaluations_noEvaluations() {
        when(this.restaurantRepository.findById(this.data.r3.getId())).thenReturn(Optional.of(this.data.r3));

        assertThat(this.evaluationService.getEvaluations(this.data.r3.getId())).isEmpty();
    }

    @Test
    void getEvaluations_notFound() {
        assertThatThrownBy(() -> this.evaluationService.getEvaluations(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEvaluation() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        assertThat(this.evaluationService.getEvaluation(this.data.r2.getId(), this.data.e3.getId())).isSameAs(this.data.e3);
    }

    @Test
    void getEvaluation_notFound() {
        assertThatThrownBy(() -> this.evaluationService.getEvaluation(this.data.r1.getId(), 999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEvaluation_fromAnotherRestaurant() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        assertThatThrownBy(() -> this.evaluationService.getEvaluation(this.data.r1.getId(), this.data.e3.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEvaluationsMe() {
        when(this.evaluationRepository.findByAuthor(LUCIEN)).thenReturn(List.of(this.data.e3));

        assertThat(this.evaluationService.getEvaluationsMe(user(LUCIEN))).containsExactly(this.data.e3);
    }

    @Test
    void getEvaluationsMe_noEvaluations() {
        when(this.evaluationRepository.findByAuthor(LUCIEN)).thenReturn(List.of());

        assertThat(this.evaluationService.getEvaluationsMe(user(LUCIEN))).isEmpty();
    }

    @Test
    void searchEvaluations() {
        when(this.indexService.searchEvaluations("pizzas")).thenReturn(List.of(this.data.e1.getId()));
        when(this.evaluationRepository.findAllById(List.of(this.data.e1.getId()))).thenReturn(List.of(this.data.e1));

        assertThat(this.evaluationService.searchEvaluations("pizzas")).containsExactly(this.data.e1);
    }

    @Test
    void searchEvaluations_noMatch() {
        when(this.indexService.searchEvaluations("sushis")).thenReturn(List.of());
        when(this.evaluationRepository.findAllById(List.of())).thenReturn(List.of());

        assertThat(this.evaluationService.searchEvaluations("sushis")).isEmpty();
    }

    @Test
    void updateEvaluation_all() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        this.evaluationService.updateEvaluation(user(LUCIEN), this.data.r2.getId(), this.data.e3.getId(), "Finalement pas mal", 2);

        assertThat(this.data.e3.getComment()).isEqualTo("Finalement pas mal");
        assertThat(this.data.e3.getRate()).isEqualTo(2);
        verify(this.evaluationRepository).save(this.data.e3);
        verify(this.indexService).deleteEvaluation(this.data.e3.getId());
        verify(this.indexService).indexEvaluation(this.data.r2.getId(), this.data.e3.getId(), "Finalement pas mal");
    }

    @Test
    void updateEvaluation_rate() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        this.evaluationService.updateEvaluation(user(LUCIEN), this.data.r2.getId(), this.data.e3.getId(), null, 2);

        assertThat(this.data.e3.getComment()).isEqualTo("Bof...");
        assertThat(this.data.e3.getRate()).isEqualTo(2);
        verify(this.indexService).indexEvaluation(this.data.r2.getId(), this.data.e3.getId(), "Bof...");
    }

    @Test
    void updateEvaluation_comment() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        this.evaluationService.updateEvaluation(user(LUCIEN), this.data.r2.getId(), this.data.e3.getId(), "Finalement pas mal", null);

        assertThat(this.data.e3.getComment()).isEqualTo("Finalement pas mal");
        assertThat(this.data.e3.getRate()).isEqualTo(1);
    }

    @Test
    void updateEvaluation_blank() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        this.evaluationService.updateEvaluation(user(LUCIEN), this.data.r2.getId(), this.data.e3.getId(), "   ", 3);

        assertThat(this.data.e3.getComment()).isEqualTo("Bof...");
        assertThat(this.data.e3.getRate()).isEqualTo(3);
    }

    @Test
    void updateEvaluation_forbidden() {
        when(this.evaluationRepository.findById(this.data.e2.getId())).thenReturn(Optional.of(this.data.e2));

        assertThatThrownBy(() -> this.evaluationService.updateEvaluation(user(LUCIEN), this.data.r1.getId(), this.data.e2.getId(), "Piraté", 3))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateEvaluation_notFound() {
        assertThatThrownBy(() -> this.evaluationService.updateEvaluation(user(LUCIEN), this.data.r2.getId(), 999, "Commentaire", 1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateEvaluation_fromAnotherRestaurant() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        assertThatThrownBy(() -> this.evaluationService.updateEvaluation(user(LUCIEN), this.data.r1.getId(), this.data.e3.getId(), "Commentaire", 1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteEvaluation() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        this.evaluationService.deleteEvaluation(user(LUCIEN), this.data.r2.getId(), this.data.e3.getId());

        verify(this.evaluationRepository).delete(this.data.e3);
        verify(this.indexService).deleteEvaluation(this.data.e3.getId());
    }

    @Test
    void deleteEvaluation_forbidden() {
        when(this.evaluationRepository.findById(this.data.e1.getId())).thenReturn(Optional.of(this.data.e1));

        assertThatThrownBy(() -> this.evaluationService.deleteEvaluation(user(LUCIEN), this.data.r1.getId(), this.data.e1.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void deleteEvaluation_notFound() {
        assertThatThrownBy(() -> this.evaluationService.deleteEvaluation(admin(NOEL), this.data.r1.getId(), 999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteEvaluation_fromAnotherRestaurant() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        assertThatThrownBy(() -> this.evaluationService.deleteEvaluation(user(LUCIEN), this.data.r1.getId(), this.data.e3.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createDishPictureUploadUrl() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));
        when(this.s3Repository.getDishPictureUploadUrl(anyString())).thenReturn("https://s3/upload");

        String url = this.evaluationService.createDishPictureUploadUrl(user(LUCIEN), this.data.r2.getId(), this.data.e3.getId());

        assertThat(url).isEqualTo("https://s3/upload");
        assertThat(this.data.e3.getDishesPictures()).hasSize(1);
        String key = this.data.e3.getDishesPictures().getFirst();
        assertThat(key).startsWith("2/3/");
        verify(this.evaluationRepository).save(this.data.e3);
        verify(this.s3Repository).getDishPictureUploadUrl(key);
    }

    @Test
    void createDishPictureUploadUrl_forbidden() {
        when(this.evaluationRepository.findById(this.data.e1.getId())).thenReturn(Optional.of(this.data.e1));

        assertThatThrownBy(() -> this.evaluationService.createDishPictureUploadUrl(user(LUCIEN), this.data.r1.getId(), this.data.e1.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void createDishPictureUploadUrl_notFound() {
        assertThatThrownBy(() -> this.evaluationService.createDishPictureUploadUrl(user(LUCIEN), this.data.r2.getId(), 999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getDishPictureUrl() {
        this.data.e3.getDishesPictures().add("2/3/photo");
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));
        when(this.s3Repository.getDishPictureUrl("2/3/photo")).thenReturn("https://s3/photo");

        assertThat(this.evaluationService.getDishPictureUrl(this.data.r2.getId(), this.data.e3.getId(), 0)).isEqualTo("https://s3/photo");
    }

    @Test
    void getDishPictureUrl_noPictures() {
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        assertThatThrownBy(() -> this.evaluationService.getDishPictureUrl(this.data.r2.getId(), this.data.e3.getId(), 0))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getDishPictureUrl_outOfBounds() {
        this.data.e3.getDishesPictures().add("2/3/photo");
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        assertThatThrownBy(() -> this.evaluationService.getDishPictureUrl(this.data.r2.getId(), this.data.e3.getId(), 1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getDishPictureUrl_negative() {
        this.data.e3.getDishesPictures().add("2/3/photo");
        when(this.evaluationRepository.findById(this.data.e3.getId())).thenReturn(Optional.of(this.data.e3));

        assertThatThrownBy(() -> this.evaluationService.getDishPictureUrl(this.data.r2.getId(), this.data.e3.getId(), -1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getDishPictureUrl_notFound() {
        assertThatThrownBy(() -> this.evaluationService.getDishPictureUrl(this.data.r2.getId(), 999, 0))
                .isInstanceOf(NotFoundException.class);
    }
}
