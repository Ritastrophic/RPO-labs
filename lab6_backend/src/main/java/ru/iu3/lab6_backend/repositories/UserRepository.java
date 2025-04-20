package ru.iu3.lab6_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.iu3.lab6_backend.models.User;

@Repository
public interface UserRepository  extends JpaRepository<User, Long>
{

}