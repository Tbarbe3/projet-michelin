package fst_sw.demo.controllers;

import fst_sw.demo.dto.series.SerieInputDTO;
import fst_sw.demo.dto.series.SerieDTO;
import fst_sw.demo.dto.series.ImageUrlDTO;
import fst_sw.demo.services.SerieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/series")
public class SerieController {

    private final SerieService serieService;

    @GetMapping
    public List<SerieDTO> getSeries() {
        return this.serieService.getSeries().stream()
                .map(SerieDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public SerieDTO getSerie(@PathVariable UUID id) {
        return SerieDTO.fromEntity(this.serieService.getSerie(id));
    }

    @PostMapping
    public SerieDTO addSerie(@Valid @RequestBody SerieInputDTO serie) {
        return SerieDTO.fromEntity(
                this.serieService.createSerie(serie.title, serie.description, serie.releaseDate));
    }

    @PatchMapping("/{id}")
    public void modifySerie(@PathVariable UUID id, @Valid @RequestBody SerieDTO serie) {
        this.serieService.updateSerie(id, serie.title, serie.description, serie.releaseDate);
    }

    @DeleteMapping("/{id}")
    public void deleteSerie(@PathVariable UUID id) {
        this.serieService.deleteSerie(id);
    }

    // Retourne un lien pour envoyer la cover sur le S3
    @PutMapping("/{id}/cover")
    public ImageUrlDTO addCover(@PathVariable UUID id) {
        return new ImageUrlDTO(this.serieService.createCoverUploadUrl(id));
    }

    @GetMapping("/{id}/cover")
    public ImageUrlDTO getCover(@PathVariable UUID id) {
        return new ImageUrlDTO(this.serieService.getCoverUrl(id));
    }
}
