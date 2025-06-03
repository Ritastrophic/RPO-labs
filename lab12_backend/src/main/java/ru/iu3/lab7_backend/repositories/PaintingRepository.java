package ru.iu3.lab7_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.iu3.lab7_backend.models.Painting;

@Repository
    public interface PaintingRepository  extends JpaRepository<Painting, Long> {

    }