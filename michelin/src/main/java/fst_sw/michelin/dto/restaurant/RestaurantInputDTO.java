package fst_sw.michelin.dto.restaurant;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantInputDTO {
    @NotBlank
    @Size(max=90)
    public String name;

    @NotBlank
    @Size(max=255)
    public String address;
}
