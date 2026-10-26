package fst_sw.demo.repositories;

import fst_sw.demo.entities.SerieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SerieRepository extends JpaRepository<SerieEntity, UUID> {}
