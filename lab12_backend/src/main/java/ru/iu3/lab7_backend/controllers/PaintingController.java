package ru.iu3.lab7_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.iu3.lab7_backend.models.Artist;
import ru.iu3.lab7_backend.models.Museum;
import ru.iu3.lab7_backend.models.Painting;
import ru.iu3.lab7_backend.repositories.ArtistRepository;
import ru.iu3.lab7_backend.repositories.MuseumRepository;
import ru.iu3.lab7_backend.repositories.PaintingRepository;
import ru.iu3.lab7_backend.tools.DataValidationException;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class PaintingController {

    @Autowired
    PaintingRepository paintingRepository;
    @Autowired
    MuseumRepository museumRepository;
    @Autowired
    ArtistRepository artistRepository;

    @GetMapping("/paintings")
    public Page<Painting> getAllPaintings(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return paintingRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/paintings/{id}")
    public ResponseEntity<Painting> getPainting(@PathVariable(value = "id") Long paintingId)
            throws DataValidationException {
        Painting painting = paintingRepository.findById(paintingId)
                .orElseThrow(() -> new DataValidationException("Картина с таким индексом не найдена"));
        return ResponseEntity.ok(painting);
    }

    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@RequestBody List<Painting> paintings) {
        Map<String, String> map = new HashMap<>();
        for (Painting p : paintings) {
            try {
                if (p.museum != null) {
                    Optional<Museum> m = museumRepository.findByName(p.museum.name);
                    if (m.isPresent()) {
                        p.museum = m.get();
                    }
                }
                if (p.artist != null) {
                    Optional<Artist> a = artistRepository.findByName(p.artist.name);
                    if (a.isPresent()) {
                        p.artist = a.get();
                    }
                }
                Painting nm = paintingRepository.save(p);
                map.put(nm.name, "success");
                System.out.println("Полученные данные: " + paintings);
            } catch (Exception ex) {
                String error;
                if (ex.getMessage().contains("paintings_name_key")) {
                    error = "paintingalreadyexists";
                } else {
                    error = "undefinederror";
                }
                map.put(p.name, error);
            }
        }
        return ResponseEntity.ok(map);
    }
    @PutMapping("/paintings/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable(value = "id") Long paintingId,
                                                   @RequestBody Painting paintingDetails) throws DataValidationException {
        try {
            Painting painting = paintingRepository.findById(paintingId)
                    .orElseThrow(() -> new DataValidationException("Картина с таким индексом не найдена"));
            painting.name = paintingDetails.name;
            paintingRepository.save(painting);
            return ResponseEntity.ok(painting);
        } catch (Exception ex) {
            if (ex.getMessage().contains("paintings_name_key"))
                throw new DataValidationException("Эта картина уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePainting(@PathVariable(value = "id") Long paintingId) {
        Optional<Painting> painting = paintingRepository.findById(paintingId);
        Map<String, Boolean> resp = new HashMap<>();
        if (painting.isPresent()) {
            paintingRepository.delete(painting.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/paintings/deletepaintings")
    public ResponseEntity<Object> deletePaintings(@RequestBody List<Painting> paintings) {
        paintingRepository.deleteAll(paintings);
        return new ResponseEntity(HttpStatus.OK);
    }
}