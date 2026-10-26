package fst_sw.michelin.controllers;

import fst_sw.michelin.dto.restaurant.RestaurantInputDTO;
import fst_sw.michelin.dto.restaurant.RestaurantDTO;
import fst_sw.michelin.dto.ImageUrlDTO;
import fst_sw.michelin.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public RestaurantDTO addRestaurant(@Valid @RequestBody RestaurantInputDTO restaurant) {
        return RestaurantDTO.fromEntity(
                this.restaurantService.createRestaurant(restaurant.name, restaurant.address));
    }

    @PatchMapping("/{id}")
    public void modifyRestaurant(@PathVariable int id, @Valid @RequestBody RestaurantDTO restaurant) {
        this.restaurantService.updateRestaurant(id, restaurant.name, restaurant.address);
    }

    @PutMapping("/{id}/image")
    public ImageUrlDTO addRestaurantImage(@PathVariable int id) {
        return new ImageUrlDTO(this.restaurantService.createRestaurantImageUploadUrl(id));
    }

    @GetMapping("/{id}/image")
    public ImageUrlDTO getRestaurantImage(@PathVariable int id) {
        return new ImageUrlDTO(this.restaurantService.getRestaurantImageUrl(id));
    }
}
