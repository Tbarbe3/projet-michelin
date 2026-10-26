package fst_sw.demo.services;

import fst_sw.demo.entities.SerieEntity;
import fst_sw.demo.exceptions.NotFoundException;
import fst_sw.demo.repositories.SerieRepository;
import fst_sw.demo.repositories.CommentRepository;
import fst_sw.demo.repositories.S3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SerieService {

    private final SerieRepository serieRepository;
    private final CommentRepository commentRepository;
    private final S3Repository s3Repository;
    private final IndexService indexService;

    public List<SerieEntity> getSeries() {
        return this.serieRepository.findAll();
    }

    public SerieEntity getSerie(UUID id) {
        return this.findSerieByID(id);
    }

    @Transactional
    public SerieEntity createSerie(String title, String description, String releaseDate) {
        SerieEntity serieEntity = new SerieEntity();
        serieEntity.setTitle(title);
        serieEntity.setDescription(description);
        serieEntity.setReleaseDate(releaseDate);

        this.serieRepository.save(serieEntity);
        return serieEntity;
    }

    @Transactional
    public void updateSerie(UUID id, String title, String description, String releaseDate) {
        SerieEntity serieEntity = this.findSerieByID(id);

        if (title != null && !title.isBlank())
            serieEntity.setTitle(title);
        if (description != null && !description.isBlank())
            serieEntity.setDescription(description);
        if (releaseDate != null && !releaseDate.isBlank())
            serieEntity.setReleaseDate(releaseDate);

        this.serieRepository.save(serieEntity);
    }

    @Transactional
    public void deleteSerie(UUID id) {
        SerieEntity serieEntity = this.findSerieByID(id);

        this.commentRepository.deleteAll(serieEntity.getComments());
        this.serieRepository.delete(serieEntity);
        this.indexService.deleteSerieComments(id);
    }

    @Transactional
    public String createCoverUploadUrl(UUID id) {
        SerieEntity serieEntity = this.findSerieByID(id);

        String key = id + "/" + UUID.randomUUID();
        serieEntity.setCover(key);
        this.serieRepository.save(serieEntity);

        return this.s3Repository.getCoverUploadUrl(key);
    }

    public String getCoverUrl(UUID id) {
        String key = this.findSerieByID(id).getCover();
        if (key == null)
            throw new NotFoundException("Serie " + id + " has no cover");

        return this.s3Repository.getCoverUrl(key);
    }

    private SerieEntity findSerieByID(UUID id) {
        return this.serieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serie " + id + " not found"));
    }
}
