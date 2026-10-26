package fst_sw.michelin.repositories;

import fst_sw.michelin.entities.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Integer> {

    List<EvaluationEntity> findByAuthor(String author);
}
