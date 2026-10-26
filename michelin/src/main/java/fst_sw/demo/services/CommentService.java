package fst_sw.demo.services;

import fst_sw.demo.entities.SerieEntity;
import fst_sw.demo.entities.CommentEntity;
import fst_sw.demo.exceptions.NotFoundException;
import fst_sw.demo.repositories.S3Repository;
import fst_sw.demo.repositories.SerieRepository;
import fst_sw.demo.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final SerieRepository serieRepository;
    private final S3Repository s3Repository;
    private final IndexService indexService;

    @Transactional
    public CommentEntity addComment(UUID serieId, String author, String content, int rate) {
        SerieEntity serie = this.findSerieByID(serieId);

        CommentEntity entity = new CommentEntity();
        entity.setSerie(serie);
        entity.setAuthor(author);
        entity.setContent(content);
        entity.setRate(rate);

        this.commentRepository.save(entity);
        this.indexService.indexComment(serieId, entity.getId(), content);
        return entity;
    }

    @Transactional(readOnly = true)
    public List<CommentEntity> getComments(UUID serieId) {
        return this.findSerieByID(serieId).getComments().stream().toList();
    }

    public CommentEntity getComment(UUID serieId, UUID commentId) {
        return this.findCommentByID(serieId, commentId);
    }

    public List<CommentEntity> searchComments(UUID serieId, String word) {
        if (!this.serieRepository.existsById(serieId))
            throw new NotFoundException("Serie " + serieId + " not found");

        return this.commentRepository.findAllById(this.indexService.searchComments(serieId, word));
    }

    @Transactional
    public void deleteComment(UUID serieId, UUID commentId) {
        CommentEntity comment = this.findCommentByID(serieId, commentId);

        this.commentRepository.delete(comment);
        this.indexService.deleteComment(commentId);
    }

    @Transactional
    public String createScreenshotUploadUrl(UUID serieId, UUID commentId) {
        CommentEntity comment = this.findCommentByID(serieId, commentId);

        String key = serieId + "/" + commentId + "/" + UUID.randomUUID();
        comment.setScreenshot(key);
        this.commentRepository.save(comment);

        return this.s3Repository.getScreenshotUploadUrl(key);
    }

    public String getScreenshotUrl(UUID serieId, UUID commentId) {
        String key = this.findCommentByID(serieId, commentId).getScreenshot();
        if (key == null)
            throw new NotFoundException("Comment " + commentId + " has no screenshot");

        return this.s3Repository.getScreenshotUrl(key);
    }

    private SerieEntity findSerieByID(UUID serieId) {
        return this.serieRepository.findById(serieId)
                .orElseThrow(() -> new NotFoundException("Serie " + serieId + " not found"));
    }

    private CommentEntity findCommentByID(UUID serieId, UUID commentId) {
        return this.commentRepository.findById(commentId)
                .filter(c -> c.getSerie().getId().equals(serieId))
                .orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
    }
}
