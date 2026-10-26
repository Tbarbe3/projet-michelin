package fst_sw.michelin.dto.restaurant;


import fst_sw.michelin.entities.EvaluationEntity;
import fst_sw.michelin.entities.RestaurantEntity;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    public int id;

    @Size(max=90)
    public String name;

    @Size(max=255)
    public String address;

    public double averageRate;

    public static RestaurantDTO fromEntity(RestaurantEntity entity) {
        return RestaurantDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .averageRate(entity.getEvaluations().stream().mapToInt(EvaluationEntity::getRate).average().orElse(-1))
                .build();
    }
}
