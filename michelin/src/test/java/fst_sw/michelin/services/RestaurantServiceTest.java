package fst_sw.michelin.services;

import fst_sw.michelin.TestData;
import fst_sw.michelin.entities.RestaurantEntity;
import fst_sw.michelin.exceptions.ForbiddenException;
import fst_sw.michelin.exceptions.NotFoundException;
import fst_sw.michelin.repositories.RestaurantRepository;
import fst_sw.michelin.repositories.S3Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class RestaurantServiceTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private S3Repository s3Repository;

    @InjectMocks
    private RestaurantService restaurantService;

    private final TestData data = new TestData();

    @Test
    void getRestaurants() {
        when(this.restaurantRepository.findAll()).thenReturn(List.of(this.data.r1, this.data.r2, this.data.r3));

        assertThat(this.restaurantService.getRestaurants())
                .extracting(RestaurantEntity::getName)
                .containsExactly("Domino's Pizza", "Brasserie L'Excelsior", "Louis XV");
    }

    @Test
    void getRestaurants_noRestaurants() {
        when(this.restaurantRepository.findAll()).thenReturn(List.of());

        assertThat(this.restaurantService.getRestaurants()).isEmpty();
    }

    @Test
    void getRestaurant() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));

        assertThat(this.restaurantService.getRestaurant(this.data.r1.getId())).isSameAs(this.data.r1);
    }

    @Test
    void getRestaurant_notFound() {
        assertThatThrownBy(() -> this.restaurantService.getRestaurant(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createRestaurant() {
        RestaurantEntity created = this.restaurantService.createRestaurant(admin(NOEL), "Brasserie L'Excelsior", "50 Rue Henri-Poincaré, 54000 Nancy");

        ArgumentCaptor<RestaurantEntity> saved = ArgumentCaptor.forClass(RestaurantEntity.class);
        verify(this.restaurantRepository).save(saved.capture());
        assertThat(saved.getValue()).isSameAs(created);
        assertThat(created.getName()).isEqualTo("Brasserie L'Excelsior");
        assertThat(created.getAddress()).isEqualTo("50 Rue Henri-Poincaré, 54000 Nancy");
        assertThat(created.getImage()).isNull();
        assertThat(created.getEvaluations()).isEmpty();
    }

    @Test
    void createRestaurant_forbidden() {
        assertThatThrownBy(() -> this.restaurantService.createRestaurant(user(LUCIEN), "Domino's Pizza", "Nancy"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateRestaurant_all() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));

        this.restaurantService.updateRestaurant(admin(NOEL), this.data.r1.getId(), "Pizza Hut", "1 Place Stanislas, 54000 Nancy");

        assertThat(this.data.r1.getName()).isEqualTo("Pizza Hut");
        assertThat(this.data.r1.getAddress()).isEqualTo("1 Place Stanislas, 54000 Nancy");
        verify(this.restaurantRepository).save(this.data.r1);
    }

    @Test
    void updateRestaurant_name() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));

        this.restaurantService.updateRestaurant(admin(NOEL), this.data.r1.getId(), "Pizza Hut", null);

        assertThat(this.data.r1.getName()).isEqualTo("Pizza Hut");
        assertThat(this.data.r1.getAddress()).isEqualTo("193 Bd d'Haussonville, 54000 Nancy");
        verify(this.restaurantRepository).save(this.data.r1);
    }

    @Test
    void updateRestaurant_address() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));

        this.restaurantService.updateRestaurant(admin(NOEL), this.data.r1.getId(), null, "1 Place Stanislas, 54000 Nancy");

        assertThat(this.data.r1.getName()).isEqualTo("Domino's Pizza");
        assertThat(this.data.r1.getAddress()).isEqualTo("1 Place Stanislas, 54000 Nancy");
        verify(this.restaurantRepository).save(this.data.r1);
    }

    @Test
    void updateRestaurant_blank() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));

        this.restaurantService.updateRestaurant(admin(NOEL), this.data.r1.getId(), "", "   ");

        assertThat(this.data.r1.getName()).isEqualTo("Domino's Pizza");
        assertThat(this.data.r1.getAddress()).isEqualTo("193 Bd d'Haussonville, 54000 Nancy");
    }

    @Test
    void updateRestaurant_forbidden() {
        assertThatThrownBy(() -> this.restaurantService.updateRestaurant(user(LUCIEN), this.data.r1.getId(), "Nom", "Adresse"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateRestaurant_notFound() {
        assertThatThrownBy(() -> this.restaurantService.updateRestaurant(admin(NOEL), 999, "Nom", "Adresse"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createRestaurantImageUploadUrl() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));
        when(this.s3Repository.getRestaurantImageUploadUrl(anyString())).thenReturn("https://s3/upload");

        String url = this.restaurantService.createRestaurantImageUploadUrl(admin(NOEL), this.data.r1.getId());

        assertThat(url).isEqualTo("https://s3/upload");
        assertThat(this.data.r1.getImage()).startsWith("1/");
        verify(this.restaurantRepository).save(this.data.r1);
        verify(this.s3Repository).getRestaurantImageUploadUrl(this.data.r1.getImage());
    }

    @Test
    void createRestaurantImageUploadUrl_forbidden() {
        assertThatThrownBy(() -> this.restaurantService.createRestaurantImageUploadUrl(user(LUCIEN), this.data.r1.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void createRestaurantImageUploadUrl_notFound() {
        assertThatThrownBy(() -> this.restaurantService.createRestaurantImageUploadUrl(admin(NOEL), 999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRestaurantImageUrl() {
        this.data.r1.setImage("1/cover");
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));
        when(this.s3Repository.getRestaurantImageUrl("1/cover")).thenReturn("https://s3/cover");

        assertThat(this.restaurantService.getRestaurantImageUrl(this.data.r1.getId())).isEqualTo("https://s3/cover");
    }

    @Test
    void getRestaurantImageUrl_noImage() {
        when(this.restaurantRepository.findById(this.data.r1.getId())).thenReturn(Optional.of(this.data.r1));

        assertThatThrownBy(() -> this.restaurantService.getRestaurantImageUrl(this.data.r1.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRestaurantImageUrl_notFound() {
        assertThatThrownBy(() -> this.restaurantService.getRestaurantImageUrl(999))
                .isInstanceOf(NotFoundException.class);
    }
}
