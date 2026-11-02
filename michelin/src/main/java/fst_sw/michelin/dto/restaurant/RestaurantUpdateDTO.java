package fst_sw.michelin.dto.restaurant;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantUpdateDTO {
    @Size(max=90)
    public String name;

    @Size(max=255)
    public String address;
}
