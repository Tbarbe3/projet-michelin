package fst_sw.michelin.services;

import fst_sw.michelin.entities.RestaurantEntity;
import fst_sw.michelin.exceptions.NotFoundException;
import fst_sw.michelin.exceptions.ForbiddenException;
import fst_sw.michelin.repositories.RestaurantRepository;
import fst_sw.michelin.repositories.S3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static fst_sw.michelin.config.SecurityConfig.isAdmin;

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

    public RestaurantEntity createRestaurant(JwtAuthenticationToken auth, String name, String address) {
        if (!isAdmin(auth))
            throw new ForbiddenException("User " + auth.getToken().getClaimAsString("preferred_username") + " is not authorized");

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(name);
        restaurant.setAddress(address);

        this.restaurantRepository.save(restaurant);
        return restaurant;
    }

    public void updateRestaurant(JwtAuthenticationToken auth, int id, String name, String address) {
        if (!isAdmin(auth))
            throw new ForbiddenException("User " + auth.getToken().getClaimAsString("preferred_username") + " is not authorized");

        RestaurantEntity restaurant = this.findRestaurantByID(id);

        if (name != null && !name.isBlank())
            restaurant.setName(name);
        if (address != null && !address.isBlank())
            restaurant.setAddress(address);

        this.restaurantRepository.save(restaurant);
    }

    public String createRestaurantImageUploadUrl(JwtAuthenticationToken auth, int id) {
        if (!isAdmin(auth))
            throw new ForbiddenException("User " + auth.getToken().getClaimAsString("preferred_username") + " is not authorized");

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
