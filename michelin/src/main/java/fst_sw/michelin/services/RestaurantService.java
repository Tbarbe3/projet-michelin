package fst_sw.michelin.services;

import fst_sw.michelin.entities.RestaurantEntity;
import fst_sw.michelin.exceptions.NotFoundException;
import fst_sw.michelin.repositories.RestaurantRepository;
import fst_sw.michelin.repositories.S3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final S3Repository s3Repository;

    public List<RestaurantEntity> getRestaurants() {
        return this.restaurantRepository.findAll();
    }

    public RestaurantEntity getRestaurant(int id) {
        return this.findRestaurantByID(id);
    }

    @Transactional
    public RestaurantEntity createRestaurant(String name, String address) {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(name);
        restaurant.setAddress(address);

        this.restaurantRepository.save(restaurant);
        return restaurant;
    }

    @Transactional
    public void updateRestaurant(int id, String name, String address) {
        RestaurantEntity restaurant = this.findRestaurantByID(id);

        if (name != null && !name.isBlank())
            restaurant.setName(name);
        if (address != null && !address.isBlank())
            restaurant.setAddress(address);

        this.restaurantRepository.save(restaurant);
    }

    @Transactional
    public String createRestaurantImageUploadUrl(int id) {
        RestaurantEntity restaurant = this.findRestaurantByID(id);

        String key = id + "/" + UUID.randomUUID();
        restaurant.setImage(key);
        this.restaurantRepository.save(restaurant);

        return this.s3Repository.getRestaurantImageUploadUrl(key);
    }

    public String getRestaurantImageUrl(int id) {
        String key = this.findRestaurantByID(id).getImage();
        if (key == null)
            throw new NotFoundException("Restaurant " + id + " has no cover");

        return this.s3Repository.getRestaurantImageUrl(key);
    }

    private RestaurantEntity findRestaurantByID(int id) {
        return this.restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant " + id + " not found"));
    }
}
