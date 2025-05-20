package ru.iu3.lab7_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.lab7_backend.models.Museum;
import ru.iu3.lab7_backend.repositories.MuseumRepository;
import ru.iu3.lab7_backend.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1/museums")
public class MuseumController {

    @Autowired
    MuseumRepository museumRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping()
    public ResponseEntity<List<Museum>> getAllMuseums() {
        return ResponseEntity.ok(museumRepository.findAll());
    }

    @PostMapping()
    public ResponseEntity<Object> createMuseum(@RequestBody Museum museum) {
        try {
            Museum nm = museumRepository.save(museum);
            return ResponseEntity.ok(nm);
        } catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("museums_name_key")) {
                error = "museumalreadyexists";
            } else {
                error = "undefinederror";
            }
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Museum> updateMuseum(@PathVariable(value = "id") Long museumId,
                                                 @RequestBody Museum museumDetails) {
        Museum museum = null;
        Optional<Museum>
                cc = museumRepository.findById(museumId);
        if (cc.isPresent()) {
            museum = cc.get();
            museum.name = museumDetails.name;
            museumRepository.save(museum);
            return ResponseEntity.ok(museum);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "museum not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMuseum(@PathVariable(value = "id") Long museumId) {
        Optional<Museum> museum = museumRepository.findById(museumId);
        Map<String, Boolean> resp = new HashMap<>();
        if (museum.isPresent()) {
            museumRepository.delete(museum.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }
}
