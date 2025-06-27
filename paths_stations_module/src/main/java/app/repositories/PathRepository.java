package app.repositories;

import app.models.Path;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PathRepository extends JpaRepository<Path, UUID> {

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false")
    List<Path> findAll();

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.id = ?1")
    Optional<Path> findById(UUID id);

    @Query("select b from #{#entityName} b where b.isDeleted = false and b.number = ?1 and b.city = ?2")
    Optional<Path> findByNumberAndCity(String number, String city);

    @Transactional
    @Override
    @Modifying
    @Query("update #{#entityName} b set b.isDeleted = true where b.id = ?1")
    void deleteById(UUID id);

    @Transactional
    @Modifying
    @Query("update #{#entityName} b set b.isDeleted = false where b.number = ?1 and b.city = ?2")
    void rejectSoftDelete(String number, String city);
}