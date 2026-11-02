package fst_sw.michelin;

import fst_sw.michelin.entities.EvaluationEntity;
import fst_sw.michelin.entities.RestaurantEntity;

public final class TestData {
    private int rId = 0;
    public final RestaurantEntity r1 = restaurant("Domino's Pizza", "193 Bd d'Haussonville, 54000 Nancy");
    public final RestaurantEntity r2 = restaurant("Brasserie L'Excelsior", "50 Rue Henri-Poincaré, 54000 Nancy");
    public final RestaurantEntity r3 = restaurant("Louis XV", "Palais du Gouverneur, Place de la Carrière, 54000 Nancy");

    private int eId = 0;
    public final EvaluationEntity e1 = evaluation(this.r1, TestToken.NOEL, "Bonnes pizzas !", 3);
    public final EvaluationEntity e2 = evaluation(this.r1, TestToken.PROF, "HORRIBLE ! Beurk !", 0);
    public final EvaluationEntity e3 = evaluation(this.r2, TestToken.LUCIEN, "Bof...", 1);

    private RestaurantEntity restaurant(String name, String address) {
        return RestaurantEntity.builder()
                .id(++this.rId)
                .name(name)
                .address(address)
                .build();
    }

    private EvaluationEntity evaluation(RestaurantEntity restaurant, String author, String comment, int rate) {
        EvaluationEntity evaluation = EvaluationEntity.builder()
                .id(++this.eId)
                .restaurant(restaurant)
                .author(author)
                .comment(comment)
                .rate(rate)
                .build();
        restaurant.getEvaluations().add(evaluation);
        return evaluation;
    }
}
