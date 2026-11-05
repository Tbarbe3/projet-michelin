package fst_sw.michelin.config;

import fst_sw.michelin.entities.RestaurantEntity;
import fst_sw.michelin.entities.EvaluationEntity;
import fst_sw.michelin.repositories.EvaluationRepository;
import fst_sw.michelin.repositories.RestaurantRepository;
import fst_sw.michelin.services.IndexService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(RestaurantRepository restaurantRepository, EvaluationRepository evaluationRepository, IndexService indexService) {
        return args -> {
            if (restaurantRepository.count() > 0)
                return;

            RestaurantEntity r1 = new RestaurantEntity();
            r1.setName("Domino's Pizza");
            r1.setAddress("193 Bd d'Haussonville, 54000 Nancy");

            RestaurantEntity r2 = new RestaurantEntity();
            r2.setName("Brasserie L'Excelsior");
            r2.setAddress("50 Rue Henri-Poincaré, 54000 Nancy");

            RestaurantEntity r3 = new RestaurantEntity();
            r3.setName("Louis XV");
            r3.setAddress("Palais du Gouverneur, Place de la Carrière, 54000 Nancy");

            restaurantRepository.saveAll(List.of(r1, r2, r3));

            EvaluationEntity e1 = new EvaluationEntity();
            e1.setRestaurant(r1);
            e1.setAuthor("noel.flantier@michelin.fr");
            e1.setComment("Bonnes pizzas !");
            e1.setRate(3);

            EvaluationEntity e2 = new EvaluationEntity();
            e2.setRestaurant(r1);
            e2.setAuthor("antoine.robert@intech.lu");
            e2.setComment("HORRIBLE ! Beurk !");
            e2.setRate(0);

            EvaluationEntity e3 = new EvaluationEntity();
            e3.setRestaurant(r2);
            e3.setAuthor("lucien.bramard@michelin.fr");
            e3.setComment("Bof...");
            e3.setRate(1);

            evaluationRepository.saveAll(List.of(e1, e2, e3));
            for (EvaluationEntity e : List.of(e1, e2, e3))
                indexService.indexEvaluation(e.getRestaurant().getId(), e.getId(), e.getComment());
        };
    }
}
