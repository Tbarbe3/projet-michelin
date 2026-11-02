package fst_sw.michelin.dto.restaurant;

import fst_sw.michelin.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantDTOTest {
    private final TestData data = new TestData();

    @Test
    void moyenne_noEvaluation() {
        assertThat(RestaurantDTO.fromEntity(this.data.r3).moyenne).isEqualTo(-1);
    }

    @Test
    void moyenne_oneEvaluation() {
        assertThat(RestaurantDTO.fromEntity(this.data.r2).moyenne).isEqualTo(1.0);
    }

    @Test
    void moyenne_someEvaluation() {
        assertThat(RestaurantDTO.fromEntity(this.data.r1).moyenne).isEqualTo(1.5); // (3+0)/2
    }
}
