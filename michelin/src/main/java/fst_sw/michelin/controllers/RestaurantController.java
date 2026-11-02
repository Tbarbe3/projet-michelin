package fst_sw.michelin.controllers;

import fst_sw.michelin.dto.restaurant.RestaurantInputDTO;
import fst_sw.michelin.dto.restaurant.RestaurantDTO;
import fst_sw.michelin.dto.ImageUrlDTO;
import fst_sw.michelin.dto.restaurant.RestaurantUpdateDTO;
import fst_sw.michelin.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    public List<RestaurantDTO> getRestaurants() {
        return this.restaurantService.getRestaurants().stream()
                .map(RestaurantDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public RestaurantDTO getRestaurant(@PathVariable int id) {
        return RestaurantDTO.fromEntity(this.restaurantService.getRestaurant(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public RestaurantDTO addRestaurant(JwtAuthenticationToken auth, @Valid @RequestBody RestaurantInputDTO restaurant) {
        return RestaurantDTO.fromEntity(this.restaurantService.createRestaurant(auth, restaurant.name, restaurant.address));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public void updateRestaurant(JwtAuthenticationToken auth, @PathVariable int id, @Valid @RequestBody RestaurantUpdateDTO restaurant) {
        this.restaurantService.updateRestaurant(auth, id, restaurant.name, restaurant.address);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/image")
    public ImageUrlDTO addRestaurantImage(JwtAuthenticationToken auth, @PathVariable int id) {
        return new ImageUrlDTO(this.restaurantService.createRestaurantImageUploadUrl(auth, id));
    }

    @GetMapping("/{id}/image")
    public ImageUrlDTO getRestaurantImage(@PathVariable int id) {
        return new ImageUrlDTO(this.restaurantService.getRestaurantImageUrl(id));
    }
}
