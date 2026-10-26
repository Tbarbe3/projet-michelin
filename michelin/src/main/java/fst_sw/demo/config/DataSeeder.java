package fst_sw.demo.config;

import fst_sw.demo.entities.SerieEntity;
import fst_sw.demo.entities.CommentEntity;
import fst_sw.demo.repositories.CommentRepository;
import fst_sw.demo.repositories.SerieRepository;
import fst_sw.demo.services.IndexService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(SerieRepository series, CommentRepository comments, IndexService indexService) {
        return args -> {
            if (series.count() > 0)
                return;

            SerieEntity breakingBad = new SerieEntity();
            breakingBad.setTitle("Breaking Bad");
            breakingBad.setDescription("A chemistry teacher turns to cooking meth");
            breakingBad.setReleaseDate("2008/01/20");

            SerieEntity dark = new SerieEntity();
            dark.setTitle("Dark");
            dark.setDescription("Time travel mystery in a small German town");
            dark.setReleaseDate("2017/12/01");

            series.saveAll(List.of(breakingBad, dark));

            CommentEntity first = new CommentEntity();
            first.setSerie(breakingBad);
            first.setAuthor("Alice");
            first.setContent("Great show");
            first.setRate(5);

            CommentEntity second = new CommentEntity();
            second.setSerie(breakingBad);
            second.setAuthor("Bob");
            second.setContent("A bit slow at first but worth it");
            second.setRate(4);

            comments.saveAll(List.of(first, second));
            for (CommentEntity comment : List.of(first, second))
                indexService.indexComment(breakingBad.getId(), comment.getId(), comment.getContent());
        };
    }
}
