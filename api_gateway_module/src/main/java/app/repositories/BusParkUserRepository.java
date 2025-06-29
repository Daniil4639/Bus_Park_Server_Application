package app.repositories;

import app.models.BusParkUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusParkUserRepository extends JpaRepository<BusParkUser, String> {

    Optional<BusParkUser> findById(String id);
}