package ru.iu3.lab7_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.iu3.lab7_backend.models.Country;
@Repository
public interface CountryRepository  extends JpaRepository<Country, Long>
{

}
