package ru.iu3.lab7_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.lab7_backend.models.Artist;
import ru.iu3.lab7_backend.models.Museum;
import ru.iu3.lab7_backend.models.Painting;
import ru.iu3.lab7_backend.repositories.ArtistRepository;
import ru.iu3.lab7_backend.repositories.MuseumRepository;
import ru.iu3.lab7_backend.repositories.PaintingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1/paintings")
public class PaintingController {

    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping()
    public ResponseEntity<List<Painting>> getAllPaintings() {
        return ResponseEntity.ok(paintingRepository.findAll());
    }

    @PostMapping()
    public ResponseEntity<Object> createPainting(@RequestBody List<Painting> paintings) {
        Map<String, String> map = new HashMap<>();
        for (Painting p : paintings) {
            try {
                if (p.museum != null) {
                    Optional<Museum> m = museumRepository.findById(p.museum.id);
                    if (m.isPresent()) {
                        p.museum = m.get();
                    }
                }
                if (p.artist != null) {
                    Optional<Artist> a = artistRepository.findById(p.artist.id);
                    if (a.isPresent()) {
                        p.artist = a.get();
                    }
                }
                Painting nm = paintingRepository.save(p);
                map.put(nm.name, "success");
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

    @PutMapping("/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable(value = "id") Long paintingId,
                                                 @RequestBody Painting paintingDetails) {
        Painting painting = null;
        Optional<Painting>
                cc = paintingRepository.findById(paintingId);
        if (cc.isPresent()) {
            painting = cc.get();
            painting.name = paintingDetails.name;
            paintingRepository.save(painting);
            return ResponseEntity.ok(painting);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "painting not found");
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
}